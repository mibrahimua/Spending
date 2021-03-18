package com.mibrahimuadev.spending.ui.backup

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.entity.BackupEntity
import com.mibrahimuadev.spending.data.entity.DriveEntity
import com.mibrahimuadev.spending.data.model.BackupDate
import com.mibrahimuadev.spending.data.model.BaseDrive
import com.mibrahimuadev.spending.data.model.GoogleAccount
import com.mibrahimuadev.spending.data.network.google.DriveServiceHelper
import com.mibrahimuadev.spending.data.repository.GoogleRepository
import com.mibrahimuadev.spending.data.workmanager.BackupWorker
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.CurrentDate.toString
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class BackupViewModel(val applicationContext: Application) : AndroidViewModel(applicationContext) {

    private val TAG = "BackupViewModel"

    private val googleRepository: GoogleRepository

    val mGoogleSignInClient: GoogleSignInClient

    private var googleSignInAccount: GoogleSignInAccount? = null

    private val _googleAccount = MutableLiveData<GoogleAccount>()
    val googleAccount: LiveData<GoogleAccount> = _googleAccount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    var isUserLoggedIn = _isUserLoggedIn

    /**
     * tak tau variable job ini berguna atau tidak LMAO
     */
    val job = Job()

    /**
     * variable mutex untuk membuat coroutine berjalan secara sequence / berurutan
     */
    val mutex = Mutex()

    init {
        Log.d("BackupViewModel", "BackupViewModel Created")

        googleRepository = GoogleRepository(getApplication())

        mGoogleSignInClient = googleRepository.getGoogleSignInClient()
    }

    /**
     * Backup Date Section
     */
    val currentDateTime = CurrentDate.getCurrentDateTime().toString("dd MMM yyyy HH:mm")

    private val _backupDate = MutableLiveData<BackupDate>()
    val backupDate: LiveData<BackupDate> = _backupDate

    fun initRequiredData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                return@withContext checkLoggedUser()
            }
            withContext(Dispatchers.IO) {
                return@withContext getBackupDate()
            }
        }
    }

    private val workManager = WorkManager.getInstance(applicationContext)

    fun doBackup() {
        workManager.enqueue(OneTimeWorkRequest.from(BackupWorker::class.java))
    }

    suspend fun checkLoggedUser() {
        var isUserExist = false
        withContext(Dispatchers.IO) {
            _isLoading.postValue(true)

            viewModelScope.launch(job) {
                googleSignInAccount = googleRepository.getGoogleSignInAccount()
                Log.d(
                    TAG,
                    "fetch GoogleRepoGetSignInAccount in thread ${Thread.currentThread().name}"
                )
            }

            val googleRepoCheckLoggedUser = viewModelScope.async(job) {
                googleRepository.checkLoggedUser(googleSignInAccount?.email)
            }

            val result = googleRepoCheckLoggedUser.await()
            if (result is Result.Success) {
                if (result.data != null) {
                    isUserExist = true
                    _googleAccount.postValue(
                        GoogleAccount(
                            userId = googleSignInAccount?.id!!,
                            userDisplayName = googleSignInAccount?.displayName!!,
                            userEmail = googleSignInAccount?.email!!
                        )
                    )
                }
            }
            _isLoading.postValue(false)
            _isUserLoggedIn.postValue(isUserExist)
            return@withContext
        }
    }

    fun insertOrUpdateLoggedUser(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            googleRepository.insertOrUpdateLoggedUser(accountEntity)
        }
    }

    fun deleteLoggedUser() {
        viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                googleRepository.deleteLoggedUser()
            }
        }
    }

    fun getBackupDate() {
        if (googleAccount.value?.userId != null) {
            viewModelScope.launch(job) {
                mutex.withLock {
                    val job1 = viewModelScope.async(job) {
                        googleRepository.getBackupDate(googleAccount.value!!.userId)
                    }
                    val result = job1.await()
                    _backupDate.postValue(
                        BackupDate(
                            localBackup = result?.localBackup,
                            googleBackup = result?.googleBackup
                        )
                    )
                }
            }
        } else {
            Log.d(TAG, "getBackupDate() : googleAccount userId is null")
        }
    }

    fun deleteBackupDate() {
        viewModelScope.launch {
            mutex.withLock {
                googleRepository.deleteBackupDate()
            }
        }
    }

    fun searchFileBackupDrive(driveService: Drive): Boolean {
        /**
         * Disini dilakukan pencarian nama file backup
         * jika file tidak ditemukan maka return false dan sebaliknya
         */
        var pageToken: String? = null
        var countFile = 0
        do {
            val result: FileList = driveService.files().list()
                .setQ("mimeType = 'application/vnd.google-apps.folder'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute()
            for (file in result.files) {
                System.out.printf(
                    "Found file: %s (%s)\n",
                    file.name, file.id
                )
                countFile += 1
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)

        return countFile > 0
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "BackupViewModel Cleared")
    }
}