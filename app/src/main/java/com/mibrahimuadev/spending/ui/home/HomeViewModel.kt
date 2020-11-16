package com.mibrahimuadev.spending.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.ListTransaksi
import com.mibrahimuadev.spending.data.repository.TransaksiRepository

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TransaksiRepository = TransaksiRepository(application)

    val allTransaksi: LiveData<Result<List<ListTransaksi>>> = repository.observeAllTransaksi()
    init {
        Log.i("HomeViewModel", "HomeViewModel created")
    }

}