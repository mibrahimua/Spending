package com.mibrahimuadev.spending.data.network.google

import android.content.Context
import androidx.work.Data
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.backup.BackupManagement
import com.mibrahimuadev.spending.data.backup.CreateJsonDbVersion
import com.mibrahimuadev.spending.data.entity.BackupEntity
import com.mibrahimuadev.spending.data.model.BaseDrive
import com.mibrahimuadev.spending.data.repository.GoogleRepository
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.CurrentDate.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class BackupDbService(val appContext: Context) {
    private val TAG = "BackupDbService"

    private val googleRepository: GoogleRepository

    private val mGoogleSignInClient: GoogleSignInClient

    private var googleSignInAccount: GoogleSignInAccount? = null

    private var driveServiceHelper: DriveServiceHelper? = null

    private var baseDrive: BaseDrive? = null

    private var listFileId = mutableListOf<String>()

    val currentDateTimewithFormat = CurrentDate.getCurrentDateTime().toString("dd MMM yyyy HH:mm")

    val currentDateTime = CurrentDate.getCurrentDateTime()


    init {
        googleRepository = GoogleRepository(appContext)

        mGoogleSignInClient = googleRepository.getGoogleSignInClient()

        googleSignInAccount = googleRepository.getGoogleSignInAccount()
    }

    val mutex = Mutex()

    /**
     * Prosedur backup database aplikasi :
     * syncFileBackupDrive {
     * 1. createLocalDirBackupDB
     * 2. changeCheckPointPragma
     * 3. copyLocalAppDbToBackupDBDir
     *
     * sampai tahap ini aplikasi butuh GoogleSignInAccount dari googleRepository
     * 4. getDriveServiceHelper
     * 5. searchFolderDrive
     * 6. searchFileBackupDrive
     * 7. uploadFileBackupDrive
     * 8. deleteOldFileBackupDrive
     * }
     */

    /**
     * yang berbeda dengan backupviewmodel
     * 1. Coroutine Scope
     * 2. tidak ada pengecekan data google sign in ke db
     *
     */

    fun createLocalDirBackupDB() {
        /**
         * applicationContext.filesDir :
         * com.mibrahimuadev.spending/files/
         */
        val folder = appContext.filesDir
        if (!File(folder, "BackupDB").isDirectory) {
            val createFolder = File(folder, "BackupDB")
            createFolder.mkdir()
        }
        Timber.d("Create folder local backup database")
    }

    /**
     * Mekanisme backup db file.db
     */
    suspend fun changeCheckPointPragma() {
        val job1 = GlobalScope.launch(Dispatchers.IO) {
            mutex.withLock {
                googleRepository.changeCheckPointPragma()
                Timber.d("Change check point Pragma WAL")
            }
        }
        job1.join()
    }

    suspend fun copyLocalAppDbToBackupDBDir(address: String) {
        val job1 = GlobalScope.launch(Dispatchers.IO) {
            mutex.withLock {
                val backupDB = File(address, "spending_database.db")
                val currentDB = appContext.getDatabasePath(AppDatabase.DB_NAME)
                if (currentDB.exists()) {
                    val src = FileInputStream(currentDB).channel
                    val dst = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    Timber.d("Copying local database")
                }
                val backupEntity = BackupEntity(
                    userId = googleSignInAccount?.id!!,
                    localBackup = currentDateTime,
                    googleBackup = null
                )
                googleRepository.insertOrUpdateBackupDate(backupEntity)
                Timber.d("insert local date backup to database with value : ${backupEntity}")
            }
        }
        job1.join()
    }
    /**
     * End Of Mekanisme backup db file.db
     */

    /**
     * Mekanisme backup db file.json
     */


    suspend fun createJsonDbToBackupDBDir() {
        val backupManagement = BackupManagement(appContext)
        val createJson: CreateJsonDbVersion = backupManagement.verifyAppVersion()

        createJson.fetchData()
        createJson.writeJSONToFile()

        val backupEntity = BackupEntity(
            userId = googleSignInAccount?.id!!,
            localBackup = currentDateTime,
            googleBackup = null
        )
        googleRepository.insertOrUpdateBackupDate(backupEntity)
        Timber.d("insert local date backup to database with value : ${backupEntity}")
    }

    /**
     * End Of Mekanisme backup db file.json
     */

    fun uploadFileBackup(): Data {
        val backup = Data.Builder()
        GlobalScope.launch {
            mutex.withLock {
                createLocalDirBackupDB()

                /**
                 * Ini tipe backup database format file.db
                 */
//            changeCheckPointPragma()
//            copyLocalAppDbToBackupDBDir(File(appContext.filesDir, "BackupDB").toString())

                /**
                 * Jika ingin mengganti backup database dengan format file.json
                 * maka di sini dilakukan pemanggilan CreateJsonDBVersionOne Class,
                 * tapi sebelum melakukan create file json harus mengecek versi dari aplikasi yang terinstall di device user
                 * sehingga nanti aplikasi akan memanggil create file json class yang sesuai
                 *
                 * Done :
                 * membuat function yang berguna untuk memanggil create file json class sesuai dengan versi aplikasi
                 * membuat function yang berguna untuk mengecek versi aplikasi
                 *
                 */
                createJsonDbToBackupDBDir()

                getDriveServiceHelper()
                if (driveServiceHelper != null) {

                    Timber.d("begin syncing proccess with Google Drive")
                    searchFolderDrive()
                    searchFileBackupDrive()
                    uploadFileBackupDrive()
                    deleteOldFileBackupDrive()
                    Timber.d("Sync with Google Drive are finished")
                    backup.putBoolean("BACKUP", true)

                } else {
                    Timber.d("driveServiceHelper is null, cannot proccess sync with Google Drive")
                    backup.putBoolean("BACKUP", false)
                }
            }
        }
        return backup.build()
    }

    suspend fun getDriveServiceHelper() {
        if (googleSignInAccount != null) {
            val job1 = GlobalScope.async(Dispatchers.IO) {
                googleRepository.getDriveServiceHelper(googleSignInAccount!!)
            }
            driveServiceHelper = job1.await()!!
            Timber.d("get driveServiceHelper from googleRepository with result : ${driveServiceHelper}")
        } else {
            Timber.d("googleSignInAccount is null, cant get driveServiceHelper")
        }
    }

    suspend fun createFolderDrive() {
        val job1 = GlobalScope.async {
            googleRepository.createFolderDrive(driveServiceHelper)
        }
        val driveEntity = job1.await()
        baseDrive = driveEntity
        Timber.d("Creating new folder Drive with result : ${baseDrive}")
    }

    suspend fun searchFolderDrive() {
        val job1 = GlobalScope.async {
            googleRepository.searchFolderDrive(driveServiceHelper)
        }
        val driveEntity: BaseDrive? = job1.await()
        if (driveEntity?.fileId.equals(null)) {
            /**
             * Buat folder drive baru
             */
            createFolderDrive()
            Timber.d("Call createFolderDrive() because file id is null")
        } else {
            baseDrive = driveEntity
            Timber.d("Retrieve file driveEntity from googleRepository with result : ${baseDrive}")
        }
    }

    suspend fun searchFileBackupDrive() {
        val job1 = GlobalScope.async {
            googleRepository.searchFileBackupDrive(driveServiceHelper)
        }
        listFileId = job1.await()!!
        Timber.d("Retrieve List file id from googleRepository with result : ${listFileId}")
    }

    suspend fun uploadFileBackupDrive() {
        if (!baseDrive?.fileId.isNullOrEmpty() && baseDrive?.fileType.equals("folder")) {
            val job1 = GlobalScope.async {
                googleRepository.uploadFileBackupDrive(
                    driveServiceHelper,
                    baseDrive?.fileId!!
                )
            }
            val driveEntity = job1.await()
            baseDrive = driveEntity
            if (!baseDrive?.fileId.isNullOrEmpty()) {
                Timber.d("Upload local database to Google Drive with result : ${baseDrive}")

                googleRepository.updateGoogleBackup(
                    googleSignInAccount?.id!!,
                    currentDateTime
                )
                Timber.d("Update date backup google to database with value : id = ${googleSignInAccount?.id!!}, datetime = ${currentDateTime}")
            } else {
                Timber.d("Failed to upload file, BaseDrive are null or empty")
            }
        } else {
            Timber.d("Failed to upload file, BaseDrive are null or empty or not equals folder")
        }
    }

    suspend fun deleteOldFileBackupDrive() {
        if (!baseDrive?.fileId.isNullOrEmpty() && baseDrive?.fileType.equals("files")) {
            if (!listFileId.isNullOrEmpty()) {
                googleRepository.deleteOldFileBackupDrive(
                    driveServiceHelper,
                    listFileId
                )
                Timber.d("Delete old file in Google Drive")
            }
        } else {
            Timber.d("Failed to delete file, BaseDrive are null or empty or not equals files")
        }
    }

    fun downloadFileBackup(): Data {
        val backup = Data.Builder()
        GlobalScope.launch {
            mutex.withLock {
                getDriveServiceHelper()
                if (driveServiceHelper != null) {

                    Timber.d("begin syncing proccess with Google Drive")
                    searchFolderDrive()
                    searchFileBackupDrive()
                    downloadFileBackupDrive()
                    Timber.d("Sync with Google Drive are finished")
                    backup.putBoolean("BACKUP", true)

                } else {
                    Timber.d("driveServiceHelper is null, cannot proccess sync with Google Drive")
                    backup.putBoolean("BACKUP", false)
                }
            }
        }
        return backup.build()
    }

    suspend fun downloadFileBackupDrive() {
        GlobalScope.launch {
            googleRepository.downloadFileBackupDrive(
                driveServiceHelper,
                listFileId[0]
            )
        }
        Timber.d("Download file backup from Google Drive")
    }

}