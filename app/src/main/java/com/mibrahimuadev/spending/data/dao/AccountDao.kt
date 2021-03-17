package com.mibrahimuadev.spending.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.entity.CategoryEntity


@Dao
interface AccountDao {

    @Query("SELECT * FROM account WHERE userEmail = :userEmail")
    fun getLoggedUser(userEmail: String? = ""): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLoggedUser(accountEntity: AccountEntity): Long

    @Update
    fun updateLoggedUser(accountEntity: AccountEntity)

    @Transaction
    suspend fun insertOrUpdateLoggedUser(accountEntity: AccountEntity) {
        val id = insertLoggedUser(accountEntity)
        if (id == -1L) updateLoggedUser(accountEntity)
    }

    @Query("DELETE FROM account")
    fun deleteLoggedUser()

    @RawQuery
    fun changeCheckpoint(supportSQLiteQuery: SupportSQLiteQuery?): Int
}