package com.mibrahimuadev.spending.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.local.TransactionLocalDataSource
import com.mibrahimuadev.spending.data.local.dao.TransactionDao
import com.mibrahimuadev.spending.data.model.TransactionList
import kotlinx.coroutines.Dispatchers
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

    fun observeAllTransaksi(): LiveData<Result<List<TransactionList>>> {
        return transactionLocalDataSource.observeAllTransactions()
    }

    suspend fun getAllTransaksi(): Result<List<TransactionList>> {
        return transactionLocalDataSource.getAllTransactions()
    }

    suspend fun insertTransaksi(transaction: Transaction) {
        return withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }
    }

    suspend fun updateTransaksi(transaction: Transaction) {
        return transactionLocalDataSource.updateTransaction(transaction)
    }

    suspend fun deleteTransaksi(idTransaksi: Long) {
        return transactionLocalDataSource.deleteTransaction(idTransaksi)
    }

}