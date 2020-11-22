package com.mibrahimuadev.spending.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.local.TransactionLocalDataSource
import com.mibrahimuadev.spending.data.model.TransactionList

class TransactionRepository(
    application: Application
) {
    private val transactionLocalDataSource: TransactionLocalDataSource

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        transactionLocalDataSource = TransactionLocalDataSource(database.transactionDao())
    }

    fun observeAllTransaksi(): LiveData<Result<List<TransactionList>>> {
        return transactionLocalDataSource.observeAllTransaksi()
    }

    suspend fun getAllTransaksi(): Result<List<TransactionList>> {
        return transactionLocalDataSource.getAllTransaksi()
    }

    suspend fun insertTransaksi(transaction: Transaction) {
        return transactionLocalDataSource.insertTransaksi(transaction)
    }

    suspend fun updateTransaksi(transaction: Transaction) {
        return transactionLocalDataSource.updateTransaksi(transaction)
    }

    suspend fun deleteTransaksi(idTransaksi: Long) {
        return transactionLocalDataSource.deleteTransaksi(idTransaksi)
    }

}