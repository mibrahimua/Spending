package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.Kategori

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori")
    fun lihatSemuaKategori(): LiveData<List<Kategori>>

    @Query("SELECT * FROM kategori WHERE idKategori = :idKategori")
    fun getKategori(idKategori: Int): List<Kategori>

    @Insert
    suspend fun insertKategori(kategori: Kategori)

    @Query("DELETE FROM kategori")
    suspend fun deleteAllKategori()
}