package com.mibrahimuadev.spending.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.local.TransactionLocalDataSource
import com.mibrahimuadev.spending.data.local.dao.TransactionDao
import com.mibrahimuadev.spending.data.model.SummaryTransaction
import com.mibrahimuadev.spending.data.model.TransactionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class TransactionRepository(
    application: Application
) {
    private val transactionLocalDataSource: TransactionLocalDataSource
    private val transactionDao: TransactionDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        transactionLocalDataSource = TransactionLocalDataSource(database.transactionDao())
        transactionDao = database.transactionDao()
    }

    fun observeAllTransactions(): LiveData<Result<List<TransactionList>>> {
        return transactionLocalDataSource.observeAllTransactions()
    }

    suspend fun getAllTransaksi(): Result<List<TransactionList>> {
        return transactionLocalDataSource.getAllTransactions()
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

    suspend fun getSummaryTransaction(startDate: String, endDate: String) : Result<SummaryTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(transactionDao.getSummaryTransaction(startDate, endDate))
            }catch (e: Exception) {
                Result.Error(e)
            }
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

    suspend fun deleteTransaksi(idTransaksi: Long) {
        return transactionLocalDataSource.deleteTransaction(idTransaksi)
    }

}