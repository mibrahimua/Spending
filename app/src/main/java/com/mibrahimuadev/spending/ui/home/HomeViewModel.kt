package com.mibrahimuadev.spending.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.data.repository.TransactionRepository

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TransactionRepository = TransactionRepository(application)

    val allTransaction: LiveData<Result<List<TransactionList>>> = repository.observeAllTransaksi()
    init {
        Log.i("HomeViewModel", "HomeViewModel created")
    }

}