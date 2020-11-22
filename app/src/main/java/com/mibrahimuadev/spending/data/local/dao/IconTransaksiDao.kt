package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.CategoryIcon

@Dao
interface IconTransaksiDao {
    @Query("SELECT * FROM icon_kategori")
    fun lihatSemuaIcon(): LiveData<List<CategoryIcon>>

    @Insert
    suspend fun insertIcon(categoryIcon: CategoryIcon)

    @Query("DELETE FROM icon_kategori")
    suspend fun deleteAllIcon()
}