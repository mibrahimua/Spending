package com.mibrahimuadev.spending.data.backup

import android.app.Application
import android.content.Context
import com.mibrahimuadev.spending.BuildConfig

class BackupManagement(val appContext: Context) {
    private var appVersion: Int = 0

    init {
        appVersion = BuildConfig.VERSION_CODE
    }

    fun verifyAppVersion(): CreateJsonDbVersion {
        if (appVersion == 1) {
            return CreateJsonDbVersionOne(appContext as Application)
        } else {
            /**
             * return the latest version
             */
            return CreateJsonDbVersionOne(appContext as Application)
        }
    }
    /**
     * setelah user melakukan backup, file backup ada di drive google
     *
     * jika user login di device lain atau re login, aplikasi akan mendownload file backup yang ada
     * di drive google
     *
     * setelah itu aplikasi membaca file backup dan membandingkan versi aplikasi yang terinstall
     * dengan versi file backup yang dimiliki
     *
     * Todo :
     * Buat class ReadJsonDbVersionOne untuk memetakan file backup json yang lalu dilakukan insert
     * ke database aplikasi yang terinstall
     */
}