package com.mibrahimuadev.spending.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.database.entity.Kategori
import com.mibrahimuadev.spending.database.entity.Transaksi

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori")
    fun lihatSemuaKategori(): LiveData<List<Kategori>>

    @Insert
    suspend fun insertKategori(kategori: Kategori)

    @Query("DELETE FROM kategori")
    suspend fun deleteAllKategori()
}