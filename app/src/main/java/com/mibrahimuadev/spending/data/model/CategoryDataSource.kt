package com.mibrahimuadev.spending.data.model

import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Category

interface CategoryDataSource {

    fun observeAllCategories(typeCategory: TransactionType): LiveData<Result<List<CategoryList>>>

    suspend fun getAllCategories(typeCategory: TransactionType): Result<List<CategoryList>>

    suspend fun getCategory(idKategori: Int): Result<CategoryList>

    suspend fun insertCategory(category: Category)

    suspend fun deleteAllCategories()

    suspend fun updateCategory(category: Category)
}