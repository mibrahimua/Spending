package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AddTransaksiViewModelFactory(
//    private val dataSource: TransaksiDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransaksiViewModel::class.java)) {
            return AddTransaksiViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}