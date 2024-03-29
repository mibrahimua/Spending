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
        """SELECT k.categoryId, k.categoryName, c.iconName, c.iconLocation 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            GROUP BY k.categoryId"""
    )
    fun observeAllCategories(): LiveData<List<CategoryList>>

    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconName, c.iconLocation 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            GROUP BY k.categoryId"""
    )
    fun getAllCategories(): List<CategoryList>

    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconName, c.iconLocation 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE categoryId = :categoriId"""
    )
    fun getCategory(categoriId: Int): CategoryList?

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("DELETE FROM category")
    suspend fun deleteAllCategories()
}