package com.mibrahimuadev.spending.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.mibrahimuadev.spending.data.network.google.BackupDbService

class BackupWorkerOneTime(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {

        return try {
            val result: Data = BackupDbService(appContext).syncFileBackupDrive()
            Result.success(result)

        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}