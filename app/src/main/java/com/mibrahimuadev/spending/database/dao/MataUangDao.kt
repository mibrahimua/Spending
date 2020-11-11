package com.mibrahimuadev.spending.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.database.entity.MataUang

@Dao
interface MataUangDao {
    @Query("SELECT * FROM mata_uang")
    fun lihatSemuaMataUang(): List<MataUang>

    @Insert
    suspend fun insertMataUang(mataUang: MataUang)

    @Query("DELETE FROM mata_uang")
    suspend fun deleteAllMataUang()
}