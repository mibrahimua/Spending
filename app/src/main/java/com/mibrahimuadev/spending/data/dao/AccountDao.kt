package com.mibrahimuadev.spending.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mibrahimuadev.spending.data.entity.AccountEntity

@Dao
interface AccountDao {

    @Query("SELECT * FROM account")
    fun getLoggedUser(): AccountEntity?

    @Insert
    fun insertLoggedUser(accountEntity: AccountEntity)

    @Update
    fun updateLoggedUser(accountEntity: AccountEntity)

    @Query("DELETE FROM account")
    fun deleteLoggedUser()
}