package com.mibrahimuadev.spending.pemasukan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mibrahimuadev.spending.database.dao.TransaksiDao
import java.lang.IllegalArgumentException

class PemasukanViewModelFactory(
//    private val dataSource: TransaksiDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PemasukanViewModel::class.java)) {
            return PemasukanViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}