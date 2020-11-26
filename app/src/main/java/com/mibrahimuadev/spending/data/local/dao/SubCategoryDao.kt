package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.SubCategory

@Dao
interface SubCategoryDao {
    @Query("SELECT * FROM sub_category")
    fun getAllSubCategories(): LiveData<List<SubCategory>>

    @Query("SELECT * FROM sub_category WHERE subCategoryId = :subCategoryId")
    fun getSubCategory(subCategoryId: Int): List<SubCategory>

    @Insert
    suspend fun insertSubCategory(subCategory: SubCategory)

    @Query("DELETE FROM sub_category")
    suspend fun deleteAllSubCategory()
}