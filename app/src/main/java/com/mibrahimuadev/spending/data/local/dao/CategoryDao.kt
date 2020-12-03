package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.model.CategoryList
import com.mibrahimuadev.spending.data.model.TransactionType

@Dao
interface CategoryDao {
    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconName, c.iconLocation, k.typeCategory 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE k.typeCategory = :typeCategory
            GROUP BY k.categoryId"""
    )
    fun observeAllCategories(typeCategory: TransactionType): LiveData<List<CategoryList>>

    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconName, c.iconLocation, k.typeCategory 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE k.typeCategory = :typeCategory
            GROUP BY k.categoryId"""
    )
    fun getAllCategories(typeCategory: TransactionType): List<CategoryList>

    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconName, c.iconLocation 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE categoryId = :categoriId"""
    )
    fun getCategory(categoriId: Int): CategoryList?

    @Query("SELECT MAX(categoryId)+1 AS lastCategoryId FROM category")
    fun getLastCategoryId(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Transaction
    suspend fun insertOrUpdate(category: Category) {
        val id = insertCategory(category)
        if (id == -1L) updateCategory(category)
    }

    @Query("DELETE FROM category")
    suspend fun deleteAllCategories()
}