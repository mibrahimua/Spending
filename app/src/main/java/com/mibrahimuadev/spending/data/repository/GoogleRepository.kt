package com.mibrahimuadev.spending.data.repository

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.dao.AccountDao
import com.mibrahimuadev.spending.data.dao.BackupDao
import com.mibrahimuadev.spending.data.dao.DriveDao
import com.mibrahimuadev.spending.data.dao.GoogleAuthDao
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.entity.BackupEntity
import com.mibrahimuadev.spending.data.entity.DriveEntity
import com.mibrahimuadev.spending.data.network.google.DriveServiceHelper
import com.mibrahimuadev.spending.data.network.google.GoogleAuthService
import com.mibrahimuadev.spending.ui.backup.DriveData
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class GoogleRepository(val application: Application) {
    private val googleAuthService: GoogleAuthService = GoogleAuthService(application)
    private val accountDao: AccountDao
    private val googleAuthDao: GoogleAuthDao
    private val driveDao: DriveDao
    private val backupDao: BackupDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        accountDao = database.accountDao()
        googleAuthDao = database.googleAuthDao()
        driveDao = database.driveDao()
        backupDao = database.backupDao()
    }

    suspend fun checkLoggedUser(userEmail: String?): Result<AccountEntity?> {
        return withContext(Dispatchers.IO) {
            Result.Success(accountDao.getLoggedUser(userEmail))
        }
    }

    suspend fun insertOrUpdateLoggedUser(accountEntity: AccountEntity) {
        return withContext(Dispatchers.IO) {
            accountDao.insertOrUpdateLoggedUser(accountEntity)
        }
    }

    suspend fun deleteLoggedUser() {
        return withContext(Dispatchers.IO) {
            accountDao.deleteLoggedUser()
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
            backupDao.updateLocalBackup(userId, googleBackup)
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleAuthService.getGoogleSignInClient()
    }

    fun getGoogleSignInAccount(): GoogleSignInAccount? {
        return googleAuthService.getLastSignedInAccount()
    }

    suspend fun getDriveServiceHelper(googleAccount: GoogleSignInAccount): DriveServiceHelper? {
        return withContext(Dispatchers.Main) {
            var mDriveServiceHelper: DriveServiceHelper? = null
            val job1 = launch {
                val credential = GoogleAccountCredential.usingOAuth2(
                    application, setOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account
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
                Log.i("GoogleDrive", "Autorisasi Sukses $mDriveServiceHelper")

                /**
                 * function dibawah menggunakan parameter result: Intent, sementara coba langsung
                 * passing GoogleSignInAccount dari viewmodel. Jadi fungsi dibawah di disable dulu
                 */
//                GoogleSignIn.getSignedInAccountFromIntent(result)
//                    .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
//                        Log.d("GoogleDrive", "Signed in as " + googleAccount.email)
//
//                        // Use the authenticated account to sign in to the Drive service.
//                        val credential = GoogleAccountCredential.usingOAuth2(
//                            application, setOf(DriveScopes.DRIVE_FILE)
//                        )
//                        /**
//                         * Disini kita bisa pakai GoogleSignInAccount dari handleSignInResult,
//                         * jadi tidak perlu pass result intent
//                         */
//                        credential.selectedAccount = googleAccount.account
//                        val googleDriveService = Drive.Builder(
//                            AndroidHttp.newCompatibleTransport(),
//                            GsonFactory(),
//                            credential
//                        )
//                            .setApplicationName("Spending")
//                            .build()
//
//                        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
//                        // Its instantiation is required before handling any onClick actions.
//                        mDriveServiceHelper = DriveServiceHelper(googleDriveService)
//                        Log.i("GoogleDrive", "Autorisasi Sukses $mDriveServiceHelper")
//                    }
//                    .addOnFailureListener { exception: java.lang.Exception? ->
//                        Log.e(
//                            "GoogleDrive",
//                            "Unable to sign in.",
//                            exception
//                        )
//                    }
            }
            job1.join()
            Log.i("GoogleDrive", "getDriveServiceHelper : $mDriveServiceHelper")

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

//    suspend fun setDetailCredentials(userId: Int) {
//        val credential = getDetailCredentials()
//        return withContext(Dispatchers.IO) {
//            googleAuthDao.insertOrUpdate(
//                GoogleAuthEntity(
//                    userId = userId,
//                    accessToken = credential!!.accessToken,
//                    refreshToken = credential!!.refreshToken
//                )
//            )
//        }
//    }


    /**
     * Below is example of call retrofit from activity
     */
//    fun exampleFromActivty() {
//        val authUserService = this.authUser()
//        authUserService.getAuth().enqueue(object : Callback<List<GoogleAuth>> {
//            override fun onResponse(
//                call: Call<List<GoogleAuth>>,
//                response: Response<List<GoogleAuth>>
//            ) {
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    Log.i("tag", "berhasil ambil data")
//                    data?.map {
//                        Log.i("tag", "datanya {${it.accessToken}}")
//                    }
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<GoogleAuth>>, t: Throwable) {
//                Log.e("tag", "gagal ambil data, errornya {${t.message}}")
//            }
//
//        })
//    }

}