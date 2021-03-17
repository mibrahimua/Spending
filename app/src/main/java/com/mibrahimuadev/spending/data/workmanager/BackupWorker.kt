package com.mibrahimuadev.spending.data.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackupWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        /**
         * Disini dilakukan pemanggilan fungsi sync
         */
        return try {

            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}