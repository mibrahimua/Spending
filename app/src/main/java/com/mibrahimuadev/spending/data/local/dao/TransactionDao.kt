package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.model.SummaryTransaction
import com.mibrahimuadev.spending.data.model.TransactionList

@Dao
interface TransactionDao {

    @Query(
        """SELECT t.transactionId, t.transactionType, t.transactionNominal, t.transactionDate, k.categoryName, c.iconLocation, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId,CAST(transactionNominal AS text) AS transactionNominalFormat
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
                k.categoryId, m.currencyId,CAST(transactionNominal AS text) AS transactionNominalFormat
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN category_icon c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
				GROUP BY t.transactionId
                ORDER BY transactionDate DESC"""
    )
    suspend fun getAllTransactions(): List<TransactionList>

    @Query(
        """SELECT t.transactionId, t.transactionType, t.transactionNominal, t.transactionDate, k.categoryName, c.iconLocation, m.currencySymbol, t.transactionNote, 
                k.categoryId, m.currencyId, CAST(transactionNominal AS text) AS transactionNominalFormat
                FROM transaction_spend t
                LEFT JOIN category k ON t.categoryId = k.categoryId
                LEFT JOIN category_icon c ON c.iconId = k.iconId
                LEFT JOIN currency m ON m.currencyId = t.currencyId
                WHERE t.transactionId = :transactionId"""
    )
    suspend fun getTransaction(transactionId: Long): TransactionList

    @Query(
        """SELECT (SELECT CAST(SUM(transactionNominal) AS text) FROM transaction_spend WHERE transactionType = 'EXPENSE') AS expenseNominal, 
                (SELECT CAST(SUM(transactionNominal) AS text) FROM transaction_spend WHERE transactionType = 'INCOME') AS incomeNominal,
                CAST(((SELECT SUM(transactionNominal) FROM transaction_spend WHERE transactionType = 'INCOME') - 
                (SELECT SUM(transactionNominal) FROM transaction_spend WHERE transactionType = 'EXPENSE')) AS TEXT) AS balanceNominal,
                transactionDate as rangeTransaction
                FROM transaction_spend
                WHERE datetime(transactionDate/1000,'unixepoch', 'localtime') BETWEEN :startDate AND :endDate
                LIMIT 1"""
    )
    suspend fun getSummaryTransaction(startDate: String, endDate: String): SummaryTransaction

    @androidx.room.Transaction
    suspend fun insertOrUpdate(transaction: Transaction) {
        val id = insertTransaction(transaction)
        if (id == -1L) updateTransaction(transaction)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    //    @Delete
    @Query("DELETE FROM transaction_spend")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM transaction_spend WHERE transactionId = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)
}