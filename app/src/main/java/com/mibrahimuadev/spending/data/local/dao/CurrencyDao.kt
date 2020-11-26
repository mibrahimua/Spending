package com.mibrahimuadev.spending.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.Currency

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency")
    fun getAllCurrencies(): List<Currency>

    @Insert
    suspend fun insertMataUang(currency: Currency)

    @Query("DELETE FROM currency")
    suspend fun deleteAllCurrencies()
}