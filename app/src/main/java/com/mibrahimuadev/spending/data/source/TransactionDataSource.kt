package com.mibrahimuadev.spending.data.source

import com.mibrahimuadev.spending.utils.Result
import com.mibrahimuadev.spending.data.entity.TransactionEntity
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.data.model.TransactionSummary

interface TransactionDataSource {

    suspend fun getAllTransactions(startDate: String, endDate: String): Result<List<Transaction>>

    suspend fun getSummaryTransaction(startDate: String,endDate: String): Result<TransactionSummary>

    suspend fun getTransaction(transactionId: Long): Result<Transaction>

    suspend fun insertTransaction(transactionEntity: TransactionEntity)

    suspend fun updateTransaction(transactionEntity: TransactionEntity)

    suspend fun insertOrUpdateTransaction(transactionEntity: TransactionEntity)

    suspend fun deleteTransaction(transactionId: Long)

}