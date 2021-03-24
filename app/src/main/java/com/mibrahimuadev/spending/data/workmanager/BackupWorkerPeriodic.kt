package com.mibrahimuadev.spending.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.mibrahimuadev.spending.data.network.google.BackupDbService
import com.mibrahimuadev.spending.utils.CurrentDate
import timber.log.Timber

class BackupWorkerPeriodic(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        /**
         * Disini dilakukan pemanggilan fungsi sync
         */
        return try {
            val currentDateTime = CurrentDate.now.time.time

            val lastBackup = inputData.getLong("last_backup", 0)
            val scheduleBackup = inputData.getInt("schedule_backup", 0)

            /**
             * scheduleBackkup = 0 = NEVER
             * Formula millis to hours :
             * divide the time value by 8.64e+7
             */
            val diffDateTime: Long = Math.round((currentDateTime - lastBackup) / 8.64e+7)

            Timber.d("Check if diffDateTime : $diffDateTime and scheduleBackup : $scheduleBackup are appropriate")
            if (diffDateTime >= scheduleBackup && scheduleBackup != 0) {
                Timber.d("Begin periodic backup worker")

                val result: Data = BackupDbService(appContext).syncFileBackupDrive()
                Result.success(result)
            } else {
                Timber.d("Cancel backup worker because constraints doesn't meet")

                Result.success()
            }

        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}