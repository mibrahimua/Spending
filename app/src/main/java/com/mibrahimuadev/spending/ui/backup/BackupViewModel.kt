package com.mibrahimuadev.spending.ui.backup

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.entity.BackupEntity
import com.mibrahimuadev.spending.data.model.BackupSchedule
import com.mibrahimuadev.spending.data.model.BackupScheduleImp
import com.mibrahimuadev.spending.data.repository.GoogleRepository
import com.mibrahimuadev.spending.data.workmanager.BackupWorkerOneTime
import com.mibrahimuadev.spending.data.workmanager.BackupWorkerPeriodic
import com.mibrahimuadev.spending.data.workmanager.RestoreWorkerOneTIme
import com.mibrahimuadev.spending.utils.CurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.TimeUnit


class BackupViewModel(val applicationContext: Application) : AndroidViewModel(applicationContext) {

    private val TAG = "BackupViewModel"

    private val BACKUP_WORKER_TAG = "BACKUP_WORKER"

    private val RESTORE_WORKER_TAG = "RESTORE_WORKER"

    private val googleRepository: GoogleRepository

    val mGoogleSignInClient: GoogleSignInClient

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    var isUserLoggedIn = _isUserLoggedIn

    /**
     * variable mutex untuk membuat coroutine berjalan secara sequence / berurutan
     */
    val mutex = Mutex()

    /**
     * Section Work Manager
     */
    private val workManager = WorkManager.getInstance(applicationContext)

    val constraintsWorks: Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    internal val outputWorkInfos: LiveData<List<WorkInfo>>


    init {
        Timber.d("BackupViewModel Created")

        googleRepository = GoogleRepository(getApplication())

        mGoogleSignInClient = googleRepository.getGoogleSignInClient()

        outputWorkInfos = workManager.getWorkInfosByTagLiveData(BACKUP_WORKER_TAG)
    }

    val loggedUserFlow: LiveData<AccountEntity> = googleRepository.getLoggedUser().asLiveData()


    /**
     * Backup Date Section
     */
    val backupDateFlow: LiveData<BackupEntity> = googleRepository.getBackupDate().asLiveData()

    val backupSchedule: LiveData<BackupSchedule> =
        googleRepository.backupScheduleConf.asLiveData()
            .map {
                BackupSchedule.valueOf(it.settingValue ?: "NEVER")
            }

    fun doBackupOneTime(): Boolean {
        /**
         * 12 Hour = 4.32e+7 millisecond
         */
        val availabeTimeBackup: Long =
            backupDateFlow.value?.googleBackup?.time?.plus(4.32e+7)?.toLong() ?: 0
        val currentDateTime: Long = CurrentDate.now.time.time

        if (currentDateTime > availabeTimeBackup) {
            Timber.d("Cannot request backup worker because last backup exceed available time backup")
            return false
        } else {
            Timber.d("Begin one time backup work request")
            val requestBackup = OneTimeWorkRequestBuilder<BackupWorkerOneTime>()
                .addTag(BACKUP_WORKER_TAG)
                .setConstraints(constraintsWorks)
                .build()

            workManager.enqueueUniqueWork(
                BACKUP_WORKER_TAG,
                ExistingWorkPolicy.REPLACE,
                requestBackup
            )
            return true
        }
    }

    fun doBackupPeriodic(backupSchedule: BackupSchedule) {
        /**
         * Kisaran waktu
         * 1. Never = do nothing about this function
         * 2. Daily = 1, TimeUnit.DAYS
         * 3. Weekly = 7, TimeUnit.DAYS
         * 4. Monthly = 30, TImeUnit.DAYS
         */

        /**
         * fungsi dipanggil ketika update setting schedule backup
         */

        val backupConf = backupSchedule.name
        val scheduleBackup: Int = BackupScheduleImp(backupConf).getIntervalWorker()
        val lastBackup: Long = backupDateFlow.value?.googleBackup?.time ?: 0

        val data = Data.Builder()
        data.putLong("last_backup", lastBackup)
        data.putInt("schedule_backup", scheduleBackup)

        Timber.d("Begin periodic backup work request with parameters : scheduleBackup = $scheduleBackup, lastBackup = $lastBackup")

        val requestBackup = PeriodicWorkRequestBuilder<BackupWorkerPeriodic>(1, TimeUnit.DAYS)
            .addTag(BACKUP_WORKER_TAG)
            .setConstraints(constraintsWorks)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            BACKUP_WORKER_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            requestBackup
        )
    }

    fun doRestoreOneTime(): Boolean {
        Timber.d("Begin one time restore work request")
        val requestRestore = OneTimeWorkRequestBuilder<RestoreWorkerOneTIme>()
            .addTag(RESTORE_WORKER_TAG)
            .setConstraints(constraintsWorks)
            .build()

        workManager.enqueueUniqueWork(
            RESTORE_WORKER_TAG,
            ExistingWorkPolicy.REPLACE,
            requestRestore
        )
        return true
    }

    fun insertOrUpdateLoggedUser(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                googleRepository.insertOrUpdateLoggedUser(accountEntity)
                Timber.d("Insert or update logged user to database with value : ${accountEntity}")
            }
        }
    }

    fun deleteLoggedUser() {
        viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                googleRepository.deleteLoggedUser()
                Timber.d("Delete logged user from database")
            }
        }
    }

    fun deleteBackupDate() {
        viewModelScope.launch {
            mutex.withLock {
                googleRepository.deleteBackupDate()
                Timber.d("Delete date backup from database")
            }
        }
    }

    fun updateBackupSchedule(backupSchedule: BackupSchedule) {
        viewModelScope.launch {

            googleRepository.updateBackupSchedule(backupSchedule)

            /**
             * Configure periodic backup work request
             */
            doBackupPeriodic(backupSchedule)

            Timber.d("Update backup schedule to database with value : ${backupSchedule}")
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

    /**
     * Helper function to call a data load function with a loading spinner; errors will trigger a
     * snackbar.
     *
     * By marking [block] as [suspend] this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling
     *              the lambda, the loading spinner will display. After completion or error, the
     *              loading spinner will stop.
     */
    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _isLoading.value = true
                block()
            } catch (error: Throwable) {
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("BackupViewModel Cleared")
    }
}