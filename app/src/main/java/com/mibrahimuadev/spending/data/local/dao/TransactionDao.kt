package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.data.model.TransactionType

@Dao
interface TransactionDao {

    @Query(
        """SELECT t.transactionId, t.transactionType, t.transactionNominal, t.transactionDate, k.categoryName, c.iconLocation, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN category_icon c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
				GROUP BY t.transactionId
                ORDER BY transactionDate DESC"""
    )
    fun observeAllTransactions(): LiveData<List<TransactionList>>

    @Query(
        """SELECT t.transactionId, t.transactionType, t.transactionNominal, t.transactionDate, k.categoryName, c.iconLocation, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN category_icon c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
				GROUP BY t.transactionId
                ORDER BY transactionDate DESC"""
    )
    suspend fun getAllTransactions(): List<TransactionList>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    //    @Delete
    @Query("DELETE FROM transaction_spend")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM transaction_spend WHERE transactionId = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)
}