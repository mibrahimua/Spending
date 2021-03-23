package com.mibrahimuadev.spending.data.model

import java.util.concurrent.TimeUnit

enum class BackupSchedule {
    NEVER,
    DAILY,
    WEEKLY,
    MONTHLY
}

class BackupScheduleImp(val schedule: String) {

    fun getIntervalWorker(): IntervalWorker {
        if (schedule == BackupSchedule.DAILY.name) {
            IntervalWorker.interval = 1
        }
        if (schedule == BackupSchedule.WEEKLY.name) {
            IntervalWorker.interval = 7
        }
        if (schedule == BackupSchedule.MONTHLY.name) {
            IntervalWorker.interval = 30
        }

        return IntervalWorker
    }

    object IntervalWorker {
        var interval: Long = 1
        var timeUnit: TimeUnit = TimeUnit.DAYS
    }
}

