package com.mibrahimuadev.spending.data.restore

import android.app.Application
import android.content.Context
import com.mibrahimuadev.spending.BuildConfig

class RestoreManagement(val appContext: Context)  {
    private var appVersion: Int = 0

    init {
        appVersion = BuildConfig.VERSION_CODE
    }

    fun verifyAppVersion(): ReadJsonDbVersion {
        if (appVersion == 1) {
            return ReadJsonDbVersionOne(appContext as Application)
        } else {
            /**
             * return the latest version
             */
            return ReadJsonDbVersionOne(appContext as Application)
        }
    }
}