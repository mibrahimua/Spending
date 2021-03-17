package com.mibrahimuadev.spending.data.source

import com.mibrahimuadev.spending.utils.wrapper.Result
import com.mibrahimuadev.spending.data.entity.CategoryEntity
import com.mibrahimuadev.spending.data.model.Category
import com.mibrahimuadev.spending.data.model.TransactionType

interface CategoryDataSource {

    suspend fun getAllCategoriesByType(typeCategory: TransactionType): Result<List<Category>>

    suspend fun getDetailCategory(idKategori: Int): Result<Category?>

    suspend fun insertCategory(categoryEntity: CategoryEntity)

    suspend fun updateCategory(categoryEntity: CategoryEntity)

    suspend fun insertOrUpdateCategory(categoryEntity: CategoryEntity)
}