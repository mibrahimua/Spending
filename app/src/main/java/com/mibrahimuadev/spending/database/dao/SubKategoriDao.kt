package com.mibrahimuadev.spending.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.database.entity.Kategori
import com.mibrahimuadev.spending.database.entity.SubKategori

@Dao
interface SubKategoriDao {
    @Query("SELECT * FROM sub_kategori")
    fun lihatSemuaSubKategori(): LiveData<List<SubKategori>>

    @Insert
    suspend fun insertSubKategori(subKategori: SubKategori)

    @Query("DELETE FROM sub_kategori")
    suspend fun deleteAllSubKategori()
}