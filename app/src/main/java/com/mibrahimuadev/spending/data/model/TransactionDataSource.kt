package com.mibrahimuadev.spending.data.model

import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction

interface TransactionDataSource {

    fun observeAllTransaksi(): LiveData<Result<List<TransactionList>>>

    suspend fun getAllTransaksi(): Result<List<TransactionList>>

    suspend fun getTransaksi(): Result<List<TransactionList>>

    suspend fun insertTransaksi(transaction: Transaction)

    suspend fun deleteAllTransaksi()

    suspend fun deleteTransaksi(idTransaksi: Long)

    suspend fun updateTransaksi(transaction: Transaction)
}