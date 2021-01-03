package com.mibrahimuadev.spending.data.dao

import androidx.room.*
import com.mibrahimuadev.spending.data.entity.TransactionEntity
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.data.model.TransactionSummary
import com.mibrahimuadev.spending.data.model.TransactionSummaryPrevious

@Dao
interface TransactionDao {

    @Query(
        """SELECT t.transactionId, t.transactionType, CAST(transactionExpense AS text) AS transactionExpense,  
                CAST(transactionIncome AS text) AS transactionIncome, t.transactionDate, k.categoryName, 
                c.iconName, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN icon_category c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
                WHERE datetime(t.transactionDate/1000,'unixepoch', 'localtime') 
                BETWEEN :startDate AND :endDate
				GROUP BY t.transactionId
                ORDER BY transactionDate DESC"""
    )
    fun getAllTransactions(startDate: String, endDate: String): List<Transaction>

    @Query(
        """SELECT t.transactionId, t.transactionType, CAST(transactionExpense AS text) AS transactionExpense,  
                CAST(transactionIncome AS text) AS transactionIncome, t.transactionDate, k.categoryName, 
                c.iconName, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN icon_category c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
                WHERE t.transactionId = :transactionId"""
    )
    suspend fun getTransaction(transactionId: Long): Transaction

    @Query(
        """SELECT :startDate as rangeTransaction, 
                COALESCE(SUM(transactionIncome),0) AS incomeNominal, 
                COALESCE(SUM(transactionExpense),0) AS expenseNominal
                FROM transaction_spend
                WHERE datetime(transactionDate/1000,'unixepoch', 'localtime') 
                BETWEEN :startDate AND :endDate
                """
    )
    suspend fun getSummaryTransaction(startDate: String, endDate: String): TransactionSummary

    @Query(
        """SELECT :startDate as rangeTransaction, 
                COALESCE(SUM(transactionIncome) - SUM(transactionExpense),0) AS previousBalanceNominal
                FROM transaction_spend
                WHERE datetime(transactionDate/1000,'unixepoch', 'localtime') 
                BETWEEN :startDate AND :endDate
                """
    )
    suspend fun getPreviousBalance(startDate: String, endDate: String): TransactionSummaryPrevious

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