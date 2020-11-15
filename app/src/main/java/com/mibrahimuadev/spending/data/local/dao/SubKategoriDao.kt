package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.SubKategori

@Dao
interface SubKategoriDao {
    @Query("SELECT * FROM sub_kategori")
    fun lihatSemuaSubKategori(): LiveData<List<SubKategori>>

    @Query("SELECT * FROM sub_kategori WHERE idKategori = :idSubKategori")
    fun getKategori(idSubKategori: Int): List<SubKategori>

    @Insert
    suspend fun insertSubKategori(subKategori: SubKategori)

    @Query("DELETE FROM sub_kategori")
    suspend fun deleteAllSubKategori()
}