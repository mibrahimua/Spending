package com.mibrahimuadev.spending.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.database.AppDatabase
import com.mibrahimuadev.spending.database.dao.TransaksiDao
import com.mibrahimuadev.spending.database.entity.Transaksi
import com.mibrahimuadev.spending.database.entity.TransaksiWithKategori
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class TransaksiRepository(
    application: Application
) {
    private lateinit var transaksiDao: TransaksiDao
    private lateinit var allTransaksi: LiveData<List<TransaksiWithKategori>>

    init {
        transaksiDao = AppDatabase.getInstance(application.applicationContext).transaksiDao()

    }

    fun lihatSemuaTransaksi(): LiveData<List<TransaksiWithKategori>> {
        allTransaksi = transaksiDao.lihatSemuaTransaksi()
        return allTransaksi
    }
    suspend fun insertTransaksi(transaksi: Transaksi) {
        transaksiDao.insertTransaksi(transaksi)
    }

//    fun updateTransaksi(transaksi: Transaksi) {
//        transaksiDao.updateTransaksi(transaksi)
//    }

//    fun deleteTransaksi(transaksi: Transaksi) {
//        transaksiDao.deleteTransaksi(transaksi)
//    }

}