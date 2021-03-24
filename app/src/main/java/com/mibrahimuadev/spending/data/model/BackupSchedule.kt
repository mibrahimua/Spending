package com.mibrahimuadev.spending.data.model

import java.util.concurrent.TimeUnit

enum class BackupSchedule {
    NEVER,
    DAILY,
    WEEKLY,
    MONTHLY
}

class BackupScheduleImp(val schedule: String) {

    /**
     * perlu diberi constraint
     * jika backup daily maka,
     * dilakukan pengecekan apakah hari itu sudah melakukan backup
     *
     * jika backup weekly maka,
     * dilakukan pengecekan apakah dalam seminggu itu sudah melakukan backup
     *
     * jika backup monthly maka,
     * dilakukan pengecekan apakah dalam sebulan itu sudah melakukan backup
     */

    fun getIntervalWorker(): Int {
        var interval = 0
        if (schedule == BackupSchedule.DAILY.name) {
            interval = 1
        }
        if (schedule == BackupSchedule.WEEKLY.name) {
            interval = 7
        }
        if (schedule == BackupSchedule.MONTHLY.name) {
            interval = 30
        }

        return interval
    }

    object IntervalWorker {
        var interval: Long = 1
        var timeUnit: TimeUnit = TimeUnit.DAYS
    }
}

