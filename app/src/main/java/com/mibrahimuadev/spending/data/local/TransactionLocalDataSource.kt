package com.mibrahimuadev.spending.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.Result.Error
import com.mibrahimuadev.spending.data.Result.Success
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.local.dao.TransactionDao
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.data.model.TransactionDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionLocalDataSource internal constructor(
    private val transactionDao: TransactionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransactionDataSource {

    override fun observeAllTransactions(): LiveData<Result<List<TransactionList>>> {
        return transactionDao.observeAllTransactions().map {
            Success(it)
        }
    }

    override suspend fun getAllTransactions(): Result<List<TransactionList>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(transactionDao.getAllTransactions())
            } catch (e: Exception) {
                Error(e)
            }
        }


    override suspend fun getTransaction(): Result<List<TransactionList>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTransactions() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }
}