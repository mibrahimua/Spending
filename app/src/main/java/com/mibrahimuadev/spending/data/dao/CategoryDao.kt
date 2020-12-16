package com.mibrahimuadev.spending.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mibrahimuadev.spending.data.entity.CategoryEntity
import com.mibrahimuadev.spending.data.model.Category
import com.mibrahimuadev.spending.data.model.TransactionType

@Dao
interface CategoryDao {
    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconId,c.iconName, k.typeCategory 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE k.typeCategory = :typeCategory
            GROUP BY k.categoryId"""
    )
    fun observeAllCategoriesByType(typeCategory: TransactionType): LiveData<List<Category>>

    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconId, c.iconName, k.typeCategory 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE k.typeCategory = :typeCategory
            GROUP BY k.categoryId"""
    )
    fun getAllCategoriesByType(typeCategory: TransactionType): List<Category>

    @Query(
        """SELECT k.categoryId, k.categoryName, c.iconId, c.iconName 
            FROM category k 
            LEFT JOIN category_icon c ON k.iconId = c.iconId 
            WHERE categoryId = :categoriId"""
    )
    fun getDetailCategory(categoriId: Int): Category?

    @Query("SELECT MAX(categoryId)+1 AS lastCategoryId FROM category")
    fun getLastCategoryId(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Update
    suspend fun updateCategory(categoryEntity: CategoryEntity)

    @Transaction
    suspend fun insertOrUpdate(categoryEntity: CategoryEntity) {
        val id = insertCategory(categoryEntity)
        if (id == -1L) updateCategory(categoryEntity)
    }

    @Query("DELETE FROM category")
    suspend fun deleteAllCategories()
}