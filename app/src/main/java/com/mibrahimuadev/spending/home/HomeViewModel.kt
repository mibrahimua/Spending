package com.mibrahimuadev.spending.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.database.AppDatabase
import com.mibrahimuadev.spending.database.entity.TransaksiKategori
import com.mibrahimuadev.spending.database.entity.TransaksiWithKategori
import com.mibrahimuadev.spending.repository.TransaksiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TransaksiRepository = TransaksiRepository(application)

    val allTransaksi: LiveData<List<TransaksiWithKategori>> = repository.lihatSemuaTransaksi()
    init {
        Log.i("HomeViewModel", "HomeViewModel created")
    }

}