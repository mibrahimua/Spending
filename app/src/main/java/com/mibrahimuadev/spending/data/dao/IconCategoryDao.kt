package com.mibrahimuadev.spending.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.IconCategoryEntity

@Dao
interface IconCategoryDao {
    @Query("SELECT * FROM icon_category")
    fun getAllIcons(): List<IconCategoryEntity>

    @Query("SELECT * FROM icon_category WHERE iconId = :iconId")
    fun getDetailIcon(iconId: Int): IconCategoryEntity

    @Query("SELECT * FROM icon_category WHERE iconName = :iconName")
    fun getDetailIconByName(iconName: String): IconCategoryEntity

    @Insert
    suspend fun insertIcon(iconCategoryEntity: IconCategoryEntity)

    @Query("DELETE FROM icon_category")
    suspend fun deleteAllIcon()
}