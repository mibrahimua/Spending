package com.mibrahimuadev.spending.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.local.CategoryLocalDataSource
import com.mibrahimuadev.spending.data.model.CategoryList
import com.mibrahimuadev.spending.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(application: Application) {
    private val categoryLocalDataSource: CategoryLocalDataSource

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
    }

    fun observeAllCategories(typeCategory: TransactionType): LiveData<Result<List<CategoryList>>> {
        return categoryLocalDataSource.observeAllCategories(typeCategory)
    }

    suspend fun getAllCategories(typeCategory: TransactionType): Result<List<CategoryList>> {
        return withContext(Dispatchers.IO) {
            categoryLocalDataSource.getAllCategories(typeCategory)
        }
    }

    suspend fun getCategory(idKategori: Int): Result<CategoryList>{
        return categoryLocalDataSource.getCategory(idKategori)
    }
}