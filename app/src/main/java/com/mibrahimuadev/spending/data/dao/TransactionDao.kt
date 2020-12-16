package com.mibrahimuadev.spending.data.dao

import androidx.room.*
import com.mibrahimuadev.spending.data.entity.TransactionEntity
import com.mibrahimuadev.spending.data.model.TransactionSummary
import com.mibrahimuadev.spending.data.model.Transaction

@Dao
interface TransactionDao {

    @Query(
        """SELECT t.transactionId, t.transactionType, CAST(transactionNominal AS text) AS transactionNominal, t.transactionDate, k.categoryName, c.iconName, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN category_icon c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
                WHERE datetime(t.transactionDate/1000,'unixepoch', 'localtime') 
                BETWEEN :startDate AND :endDate
				GROUP BY t.transactionId
                ORDER BY transactionDate DESC"""
    )
    fun getAllTransactions(startDate: String, endDate: String): List<Transaction>

    @Query(
        """SELECT t.transactionId, t.transactionType, CAST(transactionNominal AS text) AS transactionNominal, t.transactionDate, k.categoryName, c.iconName, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN category_icon c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
                WHERE t.transactionId = :transactionId"""
    )
    suspend fun getTransaction(transactionId: Long): Transaction

    @Query(
        """SELECT datetime(transactionDate/1000,'unixepoch', 'localtime') as rangeTransaction, 
                (SELECT COALESCE(SUM(transactionNominal),0) FROM transaction_spend 
                WHERE transactionType = 'EXPENSE' AND datetime(transactionDate/1000,'unixepoch', 'localtime') 
                BETWEEN :startDate AND :endDate) as expenseNominal,
                (SELECT COALESCE(SUM(transactionNominal),0) FROM transaction_spend 
                WHERE transactionType = 'INCOME' AND datetime(transactionDate/1000,'unixepoch', 'localtime') 
                BETWEEN :startDate AND :endDate) as incomeNominal
                FROM transaction_spend
                GROUP BY expenseNominal, incomeNominal"""
    )
    suspend fun getSummaryTransaction(startDate: String, endDate: String): TransactionSummary

    @androidx.room.Transaction
    suspend fun insertOrUpdate(transactionEntity: TransactionEntity) {
        val id = insertTransaction(transactionEntity)
        if (id == -1L) updateTransaction(transactionEntity)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transactionEntity: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transactionEntity: TransactionEntity)

    //    @Delete
    @Query("DELETE FROM transaction_spend")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM transaction_spend WHERE transactionId = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)
}