package com.mibrahimuadev.spending.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.dao.*
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.entity.BackupEntity
import com.mibrahimuadev.spending.data.entity.DriveEntity
import com.mibrahimuadev.spending.data.entity.SettingEntity
import com.mibrahimuadev.spending.data.model.BackupSchedule
import com.mibrahimuadev.spending.data.network.google.DriveServiceHelper
import com.mibrahimuadev.spending.data.network.google.GoogleAuthService
import com.mibrahimuadev.spending.ui.backup.DriveData
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class GoogleRepository(val appContext: Context) {
    private val googleAuthService: GoogleAuthService = GoogleAuthService(appContext)
    private val accountDao: AccountDao
    private val googleAuthDao: GoogleAuthDao
    private val driveDao: DriveDao
    private val backupDao: BackupDao
    private val settingDao: SettingDao

    init {
        val database = AppDatabase.getInstance(appContext)
        accountDao = database.accountDao()
        googleAuthDao = database.googleAuthDao()
        driveDao = database.driveDao()
        backupDao = database.backupDao()
        settingDao = database.settingDao()
    }

    suspend fun checkLoggedUser(userEmail: String?): Result<AccountEntity?> {
        return withContext(Dispatchers.IO) {
            Result.Success(accountDao.getLoggedUser(userEmail))
        }
    }

    suspend fun insertOrUpdateLoggedUser(accountEntity: AccountEntity) {
        return withContext(Dispatchers.IO) {
            accountDao.insertOrUpdateLoggedUser(accountEntity)
            settingDao.updateSettingUserId(accountEntity.userId)
        }
    }

    suspend fun deleteLoggedUser() {
        return withContext(Dispatchers.IO) {
            accountDao.deleteLoggedUser()
            settingDao.deleteAllSettingValue()
        }
    }

    suspend fun getBackupDate(userId: String): BackupEntity? {
        return withContext(Dispatchers.IO) {
            backupDao.getBackupDate(userId)
        }
    }

    suspend fun insertOrUpdateBackupDate(backupEntity: BackupEntity) {
        return withContext(Dispatchers.IO) {
            backupDao.insertOrUpdateBackupDate(backupEntity)
        }
    }

    suspend fun updateLocalBackup(userId: String, localBackup: String) {
        return withContext(Dispatchers.IO) {
            backupDao.updateLocalBackup(userId, localBackup)
        }
    }

    suspend fun updateGoogleBackup(userId: String, googleBackup: String) {
        return withContext(Dispatchers.IO) {
            backupDao.updateGoogleBackup(userId, googleBackup)
        }
    }

    suspend fun deleteBackupDate() {
        return withContext(Dispatchers.IO) {
            backupDao.deleteBackupDate()
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleAuthService.getGoogleSignInClient()
    }

    fun getGoogleSignInAccount(): GoogleSignInAccount? {
        return googleAuthService.getLastSignedInAccount()
    }

    suspend fun getDriveServiceHelper(googleAccount: GoogleSignInAccount): DriveServiceHelper? {
        return withContext(Dispatchers.IO) {
            var mDriveServiceHelper: DriveServiceHelper? = null
            val job1 = launch {
                val credential = GoogleAccountCredential.usingOAuth2(
                    appContext, setOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account
                Timber.d("Configure credential with value : ${credential.selectedAccount}")

                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential
                )
                    .setApplicationName("Spending")
                    .build()

                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                // Its instantiation is required before handling any onClick actions.
                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
                Timber.d("Authorization is success with value : ${googleDriveService}")
            }
            job1.join()
            return@withContext mDriveServiceHelper
        }
    }

    suspend fun changeCheckPointPragma() {
        return withContext(Dispatchers.IO) {
            accountDao.changeCheckpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
        }
    }

    suspend fun searchFolderDrive(driveServiceHelper: DriveServiceHelper?): DriveEntity? {
        return withContext(Dispatchers.IO) {
            Log.i("GoogleDrive", "call function searchFolderDrive from driveServiceHelper")
            val driveData = driveServiceHelper?.searchFolderDrive()
            driveData?.let { insertFolderId(it) }
            return@withContext driveData
        }
    }


    suspend fun createFolderDrive(driveServiceHelper: DriveServiceHelper?): DriveEntity? {
        return withContext(Dispatchers.IO) {
            Log.i("GoogleDrive", "call function createFolderDrive from driveServiceHelper")
            val driveEntity = driveServiceHelper?.createFolderDrive()
            driveEntity?.let { insertFolderId(it) }
            return@withContext driveEntity
        }
    }

    suspend fun insertFolderId(driveEntity: DriveEntity) {
        return withContext(Dispatchers.IO) {
            driveDao.insertFileDrive(driveEntity)
        }
    }

    suspend fun searchFileBackupDrive(driveServiceHelper: DriveServiceHelper?): MutableList<String>? {
        return withContext(Dispatchers.IO) {
            Log.i(
                "GoogleDrive",
                "call function searchFileBackupDrive from driveServiceHelper"
            )
            val fileList = driveServiceHelper?.searchFileBackupDrive()
            return@withContext fileList
        }
    }

    suspend fun uploadFileBackupDrive(
        driveServiceHelper: DriveServiceHelper?,
        folderId: String
    ): DriveData? {
        return withContext(Dispatchers.IO) {
            Log.i(
                "GoogleDrive",
                "call function uploadFileBackupDrive from driveServiceHelper, folder id = $folderId"
            )
            return@withContext driveServiceHelper?.uploadFileBackupDrive(folderId)
        }
    }

    suspend fun deleteOldFileBackupDrive(
        driveServiceHelper: DriveServiceHelper?,
        listFileId: MutableList<String>
    ) {
        return withContext(Dispatchers.IO) {
            Log.i(
                "GoogleDrive",
                "call function deleteOldFileBackupDrive from driveServiceHelper"
            )
            driveServiceHelper?.deleteOldFileBackupDrive(listFileId)
        }
    }

    suspend fun getBackupSchedule(): SettingEntity {
        return withContext(Dispatchers.IO) {
            settingDao.getSettingApp("backup_schedule")
        }
    }

    suspend fun updateBackupSchedule(backupSchedule: BackupSchedule) {
        return withContext(Dispatchers.IO) {
            settingDao.updateSettingValue("backup_schedule", backupSchedule)
        }
    }

    fun hasActiveInternetConnection(context: Context): Boolean {
        if (isNetworkAvailable(context)) {
            try {
                val urlc: HttpURLConnection =
                    URL("https://clients3.google.com/generate_204").openConnection() as HttpURLConnection
                urlc.setRequestProperty("User-Agent", "Android")
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1500
                urlc.connect()
                return (urlc.responseCode == 204 && urlc.contentLength == 0)
            } catch (e: IOException) {
                Log.e("GoogleRepository", "Error checking internet connection", e)
            }
        } else {
            Log.d("GoogleRepository", "No network available!")
        }
        return false
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}