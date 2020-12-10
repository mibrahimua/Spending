package com.mibrahimuadev.spending.data.repository

import android.app.Application
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.local.dao.TransactionDao
import com.mibrahimuadev.spending.data.model.SummaryTransaction
import com.mibrahimuadev.spending.data.model.TransactionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class TransactionRepository(
    application: Application
) {
    private val transactionDao: TransactionDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        transactionDao = database.transactionDao()
    }

    suspend fun getAllTransactions(startDate: String, endDate: String): Result<List<TransactionList>> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.observeAllTransactions(startDate, endDate))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getTransaction(transactionId: Long): Result<TransactionList> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getTransaction(transactionId))
            } catch (e: Exception) {
                Result.Error(e)
            }

        }
    }

    suspend fun getSummaryTransaction(
        startDate: String,
        endDate: String
    ): Result<SummaryTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getSummaryTransaction(startDate, endDate))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun insertOrUpdateTransaction(transaction: Transaction) {
        return withContext(Dispatchers.IO + NonCancellable) {
            transactionDao.insertOrUpdate(transaction)
        }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        return withContext(Dispatchers.IO + NonCancellable) {
            transactionDao.insertTransaction(transaction)
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        return withContext(Dispatchers.IO + NonCancellable) {
            transactionDao.updateTransaction(transaction)
        }
    }

    suspend fun deleteTransaction(transactionId: Long) {
        return withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transactionId)
        }
    }
}