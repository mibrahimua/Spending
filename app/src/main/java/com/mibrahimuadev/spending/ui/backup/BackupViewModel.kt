package com.mibrahimuadev.spending.ui.backup

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
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

    private val SCOPES: MutableList<String> =
        Collections.singletonList(Scope(Scopes.DRIVE_FILE).toString())

    private var googleSignInAccount: GoogleSignInAccount? = null

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    var isUserLoggedIn = _isUserLoggedIn

    private val _googleAccount = MutableLiveData<GoogleAccount>()
    val googleAccount: LiveData<GoogleAccount> = _googleAccount

    val job = Job()

    val mutex = Mutex()

    init {
        Log.d("BackupViewModel", "BackupViewModel Created")

        googleRepository = GoogleRepository(getApplication())

        mGoogleSignInClient = googleRepository.getGoogleSignInClient()
    }

    private val _driveServiceHelper = MutableLiveData<DriveServiceHelper>()
    val driveServiceHelper: LiveData<DriveServiceHelper> = _driveServiceHelper

    // BELUM DIGUNAKAN
//    val _accessToken = MutableLiveData<String>()
//    val accessToken: LiveData<String> = _accessToken
//
//    val _filesDrive = MutableLiveData<List<File?>>()
//    val filesDrive: LiveData<List<File?>> = _filesDrive
//
//    val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Drive Section
     */
    private lateinit var driveEntity: DriveEntity

    val _baseDrive = MutableLiveData<BaseDrive>()
    val baseDrive: LiveData<BaseDrive> = _baseDrive

    val _listFileId = MutableLiveData<MutableList<String>>()
    val listFileId: LiveData<MutableList<String>> = _listFileId

    /**
     * Backup Date Section
     */
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

    suspend fun checkLoggedUser() {
        var isUserExist = false
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Starting MainCheckLoggedUser in thread ${Thread.currentThread().name}")

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
            Log.d(TAG, "result fetch GoogleRepoCheckLoggedUser ${result}")

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
            googleRepository.deleteLoggedUser()
        }
    }

    fun createLocalFileBackup(): Boolean {
        createLocalDirBackupDB()
        changeCheckPointPragma()
        copyLocalAppDbToBackupDBDir(File(applicationContext.filesDir, "BackupDB").toString())
        val checkBackupFile =
            File(applicationContext.filesDir.absoluteFile, "BackupDB/spending_database.db")
        if (checkBackupFile.exists()) return true
        return false
    }

    fun createLocalDirBackupDB() {
        /**
         * applicationContext.filesDir
         * com.mibrahimuadev.spending/files/
         */
        val folder = applicationContext.filesDir
        if (!File(folder, "BackupDB").isDirectory) {
            val createFolder = File(folder, "BackupDB")
            createFolder.mkdir()
        }
    }

    fun changeCheckPointPragma() {
        viewModelScope.launch {
            googleRepository.changeCheckPointPragma()
        }
    }

    @Throws(IOException::class)
    fun copyLocalAppDbToBackupDBDir(address: String) {
        viewModelScope.launch(CoroutineName("MainCopyLocalAppDb")) {
            Log.d(TAG, "Starting MainCopyLocalAppDb in thread ${Thread.currentThread().name}")

            val backupDB = File(address, "spending_database.db")
            val currentDB = applicationContext.getDatabasePath(AppDatabase.DB_NAME)
            if (currentDB.exists()) {
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
            }

            val upsertBackupDate = viewModelScope.launch(CoroutineName("UpsertBackupDate")) {
                Log.d(TAG, "Starting UpsertBackupDate in thread ${Thread.currentThread().name}")

                googleRepository.insertOrUpdateBackupDate(
                    BackupEntity(
                        userId = googleAccount.value!!.userId,
                        localBackup = "16-03-2021 21:37",
                        googleBackup = null
                    )
                )
                Log.d(TAG, "fetch UpsertBackupDate in thread ${Thread.currentThread().name}")

            }
            upsertBackupDate.join()

            getBackupDate()

            /**
             * memulai proses async google drive
             */
            getDriveServiceHelper()
        }
    }

    fun getDriveServiceHelper() {
        viewModelScope.launch(Dispatchers.IO) {
            if (googleRepository.hasActiveInternetConnection(getApplication()) && googleSignInAccount != null) {
                viewModelScope.launch(Dispatchers.Main) {
                    val job1 = viewModelScope.async(Dispatchers.Main) {
                        googleRepository.getDriveServiceHelper(googleSignInAccount!!)
                    }
                    _driveServiceHelper.value = job1.await()
                    Log.i("GoogleDrive", "${driveServiceHelper.value}")
                }
            }
        }
    }

    fun syncFileBackupDrive() {
        searchFolderDrive()
        searchFileBackupDrive()
        uploadFileBackupDrive()
        deleteOldFileBackupDrive()
    }

    fun searchFolderDrive() {
        viewModelScope.launch {
            mutex.withLock {
                val job1 = viewModelScope.async {
                    googleRepository.searchFolderDrive(driveServiceHelper.value)
                }
                val driveEntity: BaseDrive? = job1.await()
                if (driveEntity?.fileId.equals(null)) {
                    createFolderDrive()
                } else {
//                this@BackupViewModel.driveEntity = driveEntity?
                    _baseDrive.postValue(driveEntity)
                    Log.i(
                        "GoogleDrive",
                        "retrive driveEntity from job1 searchFolderDrive : $driveEntity"
                    )
                }
            }
        }
    }

    fun createFolderDrive() {
        viewModelScope.launch {
            mutex.withLock {
                Log.i(
                    "GoogleDrive",
                    "call createFolderDrive from googleRepository"
                )
                val job1 = viewModelScope.async {
                    googleRepository.createFolderDrive(driveServiceHelper.value)
                }
                val driveEntity = job1.await()
                _baseDrive.postValue(driveEntity)
                Log.i(
                    "GoogleDrive",
                    "retrive driveEntity from job1 createFolderDrive : $driveEntity"
                )
            }
        }
    }

    fun searchFileBackupDrive() {
        viewModelScope.launch {
            mutex.withLock {
                val job1 = async {
                    Log.i(
                        "GoogleDrive",
                        "call searchFileBackupDrive from googleRepository"
                    )
                    googleRepository.searchFileBackupDrive(driveServiceHelper.value)
                }
                _listFileId.postValue(job1.await())
                Log.i(
                    "GoogleDrive",
                    "retrive listFileId from job1 searchFileBackupDrive"
                )
            }
        }
    }

    fun uploadFileBackupDrive() {
        viewModelScope.launch {
            mutex.withLock {
                if (!baseDrive.value?.fileId.isNullOrEmpty() && baseDrive.value?.fileType.equals("folder")) {
                    Log.i(
                        "GoogleDrive",
                        "call uploadFileBackupDrive from googleRepository with folderId = ${baseDrive.value?.fileId}"
                    )
                    val job1 = viewModelScope.async {
                        googleRepository.uploadFileBackupDrive(
                            driveServiceHelper.value,
                            baseDrive.value?.fileId!!
                        )
                    }
                    val driveEntity = job1.await()
                    _baseDrive.postValue(driveEntity)
                } else {
                    Log.i("GoogleDrive", "baseDrive are null or empty or not equals folder")
                }
            }
        }
    }

    fun deleteOldFileBackupDrive() {
        viewModelScope.launch {
            mutex.withLock {
                if (!listFileId.value.isNullOrEmpty()) {
                    Log.i(
                        "GoogleDrive",
                        "call function deleteOldFileBackupDrive listFileid = ${listFileId.value}"
                    )

                    googleRepository.deleteOldFileBackupDrive(
                        driveServiceHelper.value,
                        listFileId.value!!
                    )
                }
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

    fun getBackupDate() {
        if (googleAccount.value?.userId != null) {
            viewModelScope.launch(job) {
                Log.d(TAG, "Starting MainGetBackupDate in thread ${Thread.currentThread().name}")

                val job1 = viewModelScope.async(job) {
                    googleRepository.getBackupDate(googleAccount.value!!.userId)
                }
                Log.d(TAG, "fetch googleRepoGetBackupDate in thread ${Thread.currentThread().name}")

                val result = job1.await()

                _backupDate.postValue(
                    BackupDate(
                        localBackup = result?.localBackup,
                        googleBackup = result?.googleBackup
                    )
                )
                Log.d(TAG, "result fetch of googleRepoGetBackupDate ${result}")
            }
        } else {
            Log.d(TAG, "getBackupDate() : googleAccount userId is null")
        }
    }

    fun insertOrUpdateBackupDate(backupEntity: BackupEntity) {
        viewModelScope.launch {
            googleRepository.insertOrUpdateBackupDate(backupEntity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "BackupViewModel Cleared")
    }
}