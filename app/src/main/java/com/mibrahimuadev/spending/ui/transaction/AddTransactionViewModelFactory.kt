package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AddTransactionViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            return AddTransactionViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}