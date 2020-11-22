package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.model.CategoryList

@Dao
interface CategoryDao {
    @Query(
        """SELECT k.idKategori, k.namaKategori, c.namaIcon, c.lokasiIcon 
            FROM kategori k 
            LEFT JOIN icon_kategori c ON k.idIcon = c.idIcon 
            GROUP BY k.idKategori"""
    )
    fun observeAllCategories(): LiveData<List<CategoryList>>

    @Query(
        """SELECT k.idKategori, k.namaKategori, c.namaIcon, c.lokasiIcon 
            FROM kategori k 
            LEFT JOIN icon_kategori c ON k.idIcon = c.idIcon 
            GROUP BY k.idKategori"""
    )
    fun getAllCategories(): List<CategoryList>

    @Query("SELECT * FROM kategori WHERE idKategori = :idKategori")
    fun getCategory(idKategori: Int): List<Category>

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("DELETE FROM kategori")
    suspend fun deleteAllCategories()
}