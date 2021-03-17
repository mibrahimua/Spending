package com.mibrahimuadev.spending.data.network.google

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.entity.BackupEntity
import com.mibrahimuadev.spending.data.model.BaseDrive
import com.mibrahimuadev.spending.data.model.GoogleAccount
import com.mibrahimuadev.spending.data.repository.GoogleRepository
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.CurrentDate.toString
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class BackupDbService(val appContext: Context) {
    private val TAG = "BackupDbService"

    private val googleRepository: GoogleRepository

    private val mGoogleSignInClient: GoogleSignInClient

    private var googleSignInAccount: GoogleSignInAccount? = null

//    private lateinit var googleAccount: GoogleAccount

    private var driveServiceHelper: DriveServiceHelper? = null

    private var baseDrive: BaseDrive? = null

    private var listFileId = mutableListOf<String>()

    val currentDateTime = CurrentDate.getCurrentDateTime().toString("dd MMM yyyy HH:mm")

    init {
        googleRepository = GoogleRepository(appContext)

        mGoogleSignInClient = googleRepository.getGoogleSignInClient()

        googleSignInAccount = googleRepository.getGoogleSignInAccount()
    }

    val mutex = Mutex()

    /**
     * Prosedur backup database aplikasi :
     * createLocalFileBackup {
     * 1. createLocalDirBackupDB
     * 2. changeCheckPointPragma
     * 3. copyLocalAppDbToBackupDBDir
     *}
     *
     * sampai tahap ini aplikasi butuh GoogleSignInAccount dari googleRepository
     * syncFileBackupDrive {
     * 4. getDriveServiceHelper
     * 5. searchFolderDrive
     * 6. searchFileBackupDrive
     * 7. uploadFileBackupDrive
     * 8. deleteOldFileBackupDrive
     * }
     */

    fun createLocalFileBackup(): Boolean {
        createLocalDirBackupDB()
        changeCheckPointPragma()
        copyLocalAppDbToBackupDBDir(File(appContext.filesDir, "BackupDB").toString())
        val checkBackupFile =
            File(appContext.filesDir.absoluteFile, "BackupDB/spending_database.db")
        if (checkBackupFile.exists()) return true
        return false
    }

    fun createLocalDirBackupDB() {
        /**
         * applicationContext.filesDir
         * com.mibrahimuadev.spending/files/
         */
        val folder = appContext.filesDir
        if (!File(folder, "BackupDB").isDirectory) {
            val createFolder = File(folder, "BackupDB")
            createFolder.mkdir()
        }
    }

    fun changeCheckPointPragma() {
        GlobalScope.launch {
            googleRepository.changeCheckPointPragma()
        }
    }

    @Throws(IOException::class)
    fun copyLocalAppDbToBackupDBDir(address: String) {
        GlobalScope.launch(CoroutineName("MainCopyLocalAppDb")) {
            Log.d(TAG, "Starting MainCopyLocalAppDb in thread ${Thread.currentThread().name}")

            val backupDB = File(address, "spending_database.db")
            val currentDB = appContext.getDatabasePath(AppDatabase.DB_NAME)
            if (currentDB.exists()) {
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
            }

            val upsertBackupDate = GlobalScope.launch(CoroutineName("UpsertBackupDate")) {
                Log.d(TAG, "Starting UpsertBackupDate in thread ${Thread.currentThread().name}")


                googleRepository.insertOrUpdateBackupDate(
                    BackupEntity(
                        userId = googleSignInAccount?.id!!,
                        localBackup = currentDateTime,
                        googleBackup = null
                    )
                )
                Log.d(TAG, "fetch UpsertBackupDate in thread ${Thread.currentThread().name}")

            }
            upsertBackupDate.join()

            /**
             * memulai proses async google drive
             */
            getDriveServiceHelper()
            delay(3000)
            if(driveServiceHelper != null) {
                syncFileBackupDrive()
            }
        }
    }

    fun getDriveServiceHelper() {
        GlobalScope.launch(Dispatchers.IO) {
            mutex.withLock {
                if (googleSignInAccount != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val job1 = GlobalScope.async(Dispatchers.IO) {
                            googleRepository.getDriveServiceHelper(googleSignInAccount!!)
                        }
                        driveServiceHelper = job1.await()!!
                        Log.i("GoogleDrive", "${driveServiceHelper}")
                    }
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
        GlobalScope.launch {
            mutex.withLock {
                val job1 = GlobalScope.async {
                    googleRepository.searchFolderDrive(driveServiceHelper)
                }
                val driveEntity: BaseDrive? = job1.await()
                if (driveEntity?.fileId.equals(null)) {
                    /**
                     * Buat folder drive baru
                     */
                    createFolderDrive()
                } else {
                    baseDrive = driveEntity
                    Log.i(
                        "GoogleDrive",
                        "retrive driveEntity from job1 searchFolderDrive : $driveEntity"
                    )
                }
            }
        }
    }

    fun createFolderDrive() {
        GlobalScope.launch {
            mutex.withLock {
                Log.i(
                    "GoogleDrive",
                    "call createFolderDrive from googleRepository"
                )
                val job1 = GlobalScope.async {
                    googleRepository.createFolderDrive(driveServiceHelper)
                }
                val driveEntity = job1.await()
                baseDrive = driveEntity
                Log.i(
                    "GoogleDrive",
                    "retrive driveEntity from job1 createFolderDrive : $driveEntity"
                )
            }
        }
    }

    fun searchFileBackupDrive() {
        GlobalScope.launch {
            mutex.withLock {
                val job1 = async {
                    Log.i(
                        "GoogleDrive",
                        "call searchFileBackupDrive from googleRepository"
                    )
                    googleRepository.searchFileBackupDrive(driveServiceHelper)
                }
                listFileId = job1.await()!!
                Log.i(
                    "GoogleDrive",
                    "retrive listFileId from job1 searchFileBackupDrive"
                )
            }
        }
    }

    fun uploadFileBackupDrive() {
        GlobalScope.launch {
            mutex.withLock {
                if (!baseDrive?.fileId.isNullOrEmpty() && baseDrive?.fileType.equals("folder")) {
                    Log.i(
                        "GoogleDrive",
                        "call uploadFileBackupDrive from googleRepository with folderId = ${baseDrive?.fileId}"
                    )
                    val job1 = GlobalScope.async {
                        googleRepository.uploadFileBackupDrive(
                            driveServiceHelper,
                            baseDrive?.fileId!!
                        )
                    }
                    val driveEntity = job1.await()
                    baseDrive = driveEntity

                    googleRepository.updateGoogleBackup(
                        googleSignInAccount?.id!!,
                        currentDateTime
                    )
                } else {
                    Log.i("GoogleDrive", "baseDrive are null or empty or not equals folder")
                }
            }
        }
    }

    fun deleteOldFileBackupDrive() {
        GlobalScope.launch {
            mutex.withLock {
                if (!listFileId.isNullOrEmpty()) {
                    Log.i(
                        "GoogleDrive",
                        "call function deleteOldFileBackupDrive listFileid = ${listFileId}"
                    )

                    googleRepository.deleteOldFileBackupDrive(
                        driveServiceHelper,
                        listFileId
                    )
                }
            }
        }
    }
}