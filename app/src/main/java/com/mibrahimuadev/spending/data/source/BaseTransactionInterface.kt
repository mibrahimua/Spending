package com.mibrahimuadev.spending.data.source

import com.mibrahimuadev.spending.data.entity.TransactionEntity
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.utils.wrapper.Result

interface BaseTransactionInterface {

    suspend fun getTransaction(transactionId: Long): Result<Transaction>

    suspend fun insertTransaction(transactionEntity: TransactionEntity)

    suspend fun updateTransaction(transactionEntity: TransactionEntity)

    suspend fun insertOrUpdateTransaction(transactionEntity: TransactionEntity)

    suspend fun deleteTransaction(transactionId: Long)
}