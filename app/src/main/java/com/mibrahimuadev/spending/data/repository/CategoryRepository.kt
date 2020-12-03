package com.mibrahimuadev.spending.data.repository

import android.app.Application
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.local.dao.CategoryDao
import com.mibrahimuadev.spending.data.model.CategoryList
import com.mibrahimuadev.spending.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class CategoryRepository(application: Application) {
    private val categoryDao: CategoryDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        categoryDao = database.categoryDao()
    }

    suspend fun getAllCategories(typeCategory: TransactionType): Result<List<CategoryList>> {
        return withContext(Dispatchers.IO) {
            Result.Success(categoryDao.getAllCategories(typeCategory))
        }
    }

    suspend fun getCategory(idKategori: Int): Result<CategoryList?> {
        return withContext(Dispatchers.IO) {
            Result.Success(categoryDao.getCategory(idKategori))
        }
    }

    suspend fun getLastCategoryId(): Result<Int> {
        return withContext(Dispatchers.IO) {
            Result.Success(categoryDao.getLastCategoryId())
        }
    }

    suspend fun insertOrUpdateCategory(category: Category) {
        return withContext(Dispatchers.IO + NonCancellable) {
            categoryDao.insertOrUpdate(category)
        }
    }
}