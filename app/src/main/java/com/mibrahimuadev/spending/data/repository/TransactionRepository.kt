package com.mibrahimuadev.spending.data.repository

import android.app.Application
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.dao.TransactionDao
import com.mibrahimuadev.spending.data.entity.TransactionEntity
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.data.model.TransactionSummary
import com.mibrahimuadev.spending.data.model.TransactionSummaryPrevious
import com.mibrahimuadev.spending.data.source.TransactionRepositoryInterface
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class TransactionRepository(
    application: Application
) : TransactionRepositoryInterface {
    private val transactionDao: TransactionDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        transactionDao = database.transactionDao()
    }

    override suspend fun getAllTransactions(
        startDate: String,
        endDate: String
    ): Result<List<Transaction>> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getAllTransactions(startDate, endDate))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getTransaction(transactionId: Long): Result<Transaction> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getTransaction(transactionId))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getSummaryTransaction(
        startDate: String,
        endDate: String
    ): Result<TransactionSummary> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getSummaryTransaction(startDate, endDate))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getPreviousSummaryTransaction(
        startDate: String,
        endDate: String
    ): Result<TransactionSummaryPrevious> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getPreviousBalance(startDate, endDate))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun insertOrUpdateTransaction(transactionEntity: TransactionEntity) {
        return withContext(Dispatchers.IO + NonCancellable) {
            transactionDao.insertOrUpdate(transactionEntity)
        }
    }

    override suspend fun insertTransaction(transactionEntity: TransactionEntity) {
        return withContext(Dispatchers.IO + NonCancellable) {
            transactionDao.insertTransaction(transactionEntity)
        }
    }

    override suspend fun updateTransaction(transactionEntity: TransactionEntity) {
        return withContext(Dispatchers.IO + NonCancellable) {
            transactionDao.updateTransaction(transactionEntity)
        }
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        return withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transactionId)
        }
    }
}