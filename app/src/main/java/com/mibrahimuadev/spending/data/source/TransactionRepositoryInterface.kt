package com.mibrahimuadev.spending.data.source

import com.mibrahimuadev.spending.utils.wrapper.Result
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.data.model.TransactionSummary
import com.mibrahimuadev.spending.data.model.TransactionSummaryPrevious

interface TransactionRepositoryInterface: BaseTransactionInterface {

    suspend fun getAllTransactions(startDate: String, endDate: String): Result<List<Transaction>>

    suspend fun getSummaryTransaction(startDate: String,endDate: String): Result<TransactionSummary>

    suspend fun getPreviousSummaryTransaction(startDate: String, endDate: String): Result<TransactionSummaryPrevious>

}