package com.mibrahimuadev.spending.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionsApp(val requireActivity: FragmentActivity) {

    fun checkPermissions() {

        val readExternalStorage = ContextCompat.checkSelfPermission(
            requireActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val writeExternalStorage = ContextCompat.checkSelfPermission(
            requireActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val allPermissionsGranted = readExternalStorage && writeExternalStorage

        if (!allPermissionsGranted) {
            requestReadWriteExternalStorage()
        }
    }

    private fun requestReadWriteExternalStorage() {
        ActivityCompat.requestPermissions(
            requireActivity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }
}