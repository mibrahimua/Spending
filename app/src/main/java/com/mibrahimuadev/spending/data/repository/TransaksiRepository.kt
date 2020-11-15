package com.mibrahimuadev.spending.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaksi
import com.mibrahimuadev.spending.data.local.TransaksiLocalDataSource
import com.mibrahimuadev.spending.data.model.ListTransaksi

class TransaksiRepository(
    application: Application
) {
    private val transaksiLocalDataSource: TransaksiLocalDataSource

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        transaksiLocalDataSource = TransaksiLocalDataSource(database.transaksiDao())
    }

    fun observeAllTransaksi(): LiveData<Result<List<ListTransaksi>>> {
        return transaksiLocalDataSource.observeAllTransaksi()
    }

    suspend fun getAllTransaksi(): Result<List<ListTransaksi>> {
        return transaksiLocalDataSource.getAllTransaksi()
    }

    suspend fun insertTransaksi(transaksi: Transaksi) {
        return transaksiLocalDataSource.insertTransaksi(transaksi)
    }

    suspend fun updateTransaksi(transaksi: Transaksi) {
        return transaksiLocalDataSource.updateTransaksi(transaksi)
    }

    suspend fun deleteTransaksi(idTransaksi: Long) {
        return transaksiLocalDataSource.deleteTransaksi(idTransaksi)
    }

}