package com.mibrahimuadev.spending.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mibrahimuadev.spending.data.network.google.BackupDbService

class BackupWorker(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        /**
         * Disini dilakukan pemanggilan fungsi sync
         */
        return try {
            BackupDbService(appContext).createLocalFileBackup()
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}