package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.CategoryIcon

@Dao
interface TransactionIconDao {
    @Query("SELECT * FROM category_icon")
    fun getAllIcons(): LiveData<List<CategoryIcon>>

    @Insert
    suspend fun insertIcon(categoryIcon: CategoryIcon)

    @Query("DELETE FROM category_icon")
    suspend fun deleteAllIcon()
}