package com.mibrahimuadev.spending.data.model

import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction

interface TransactionDataSource {

    fun observeAllTransactions(): LiveData<Result<List<TransactionList>>>

    suspend fun getAllTransactions(): Result<List<TransactionList>>

    suspend fun getTransaction(): Result<List<TransactionList>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun deleteAllTransactions()

    suspend fun deleteTransaction(transactionId: Long)

    suspend fun updateTransaction(transaction: Transaction)
}