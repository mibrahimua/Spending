package com.mibrahimuadev.spending.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.CurrencyEntity

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency")
    fun getAllCurrencies(): List<CurrencyEntity>

    @Insert
    suspend fun insertMataUang(currencyEntity: CurrencyEntity)

    @Query("DELETE FROM currency")
    suspend fun deleteAllCurrencies()
}