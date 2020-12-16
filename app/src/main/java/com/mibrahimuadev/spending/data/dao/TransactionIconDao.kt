package com.mibrahimuadev.spending.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.CategoryIconEntity

@Dao
interface TransactionIconDao {
    @Query("SELECT * FROM category_icon")
    fun getAllIcons(): LiveData<List<CategoryIconEntity>>

    @Insert
    suspend fun insertIcon(categoryIconEntity: CategoryIconEntity)

    @Query("DELETE FROM category_icon")
    suspend fun deleteAllIcon()
}