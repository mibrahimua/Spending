package com.mibrahimuadev.spending.pemasukan

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.database.AppDatabase
import com.mibrahimuadev.spending.database.entity.Kategori
import com.mibrahimuadev.spending.database.entity.Transaksi
import com.mibrahimuadev.spending.repository.KategoriRepository
import com.mibrahimuadev.spending.repository.TransaksiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PemasukanViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var transaksiResults: LiveData<List<Transaksi>>

    private lateinit var allKategori: LiveData<List<Kategori>>

    private val transaksiRepository: TransaksiRepository
//    private val kategoriRepository: KategoriRepository


    init {
        Log.i("PemasukanViewModel", "PemasukanViewModel created")
//        val database = AppDatabase.getInstance(application)
//        val transaksiDao = database.transaksiDao()
//        val kategoriDao = database.kategoriDao()
        transaksiRepository = TransaksiRepository(application)
//        kategoriRepository = KategoriRepository(kategoriDao)
    }

    private val _navigateFromHome = MutableLiveData<Boolean?>()

    fun insertTransaksi(transaksi: Transaksi) {
        viewModelScope.launch(Dispatchers.IO) {
            transaksiRepository.insertTransaksi(transaksi)
        }
    }

//    fun lihatSemuaKategori():LiveData<List<Kategori>> {
//        return kategoriRepository.allKategori
//    }

    val navigateFromHome: LiveData<Boolean?>
        get() = _navigateFromHome

    override fun onCleared() {
        super.onCleared()
        Log.i("PemasukanViewModel", "PemasukanViewModel destroyed")
    }
}