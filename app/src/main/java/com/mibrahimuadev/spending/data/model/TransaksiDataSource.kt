package com.mibrahimuadev.spending.data.model

import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaksi

interface TransaksiDataSource {

    fun observeAllTransaksi(): LiveData<Result<List<ListTransaksi>>>

    suspend fun getAllTransaksi(): Result<List<ListTransaksi>>

    suspend fun getTransaksi(): Result<List<ListTransaksi>>

    suspend fun insertTransaksi(transaksi: Transaksi)

    suspend fun deleteAllTransaksi()

    suspend fun deleteTransaksi(idTransaksi: Long)

    suspend fun updateTransaksi(transaksi: Transaksi)
}