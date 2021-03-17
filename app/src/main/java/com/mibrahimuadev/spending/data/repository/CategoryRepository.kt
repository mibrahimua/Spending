package com.mibrahimuadev.spending.data.repository

import android.app.Application
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.utils.wrapper.Result
import com.mibrahimuadev.spending.data.entity.CategoryEntity
import com.mibrahimuadev.spending.data.dao.CategoryDao
import com.mibrahimuadev.spending.data.source.CategoryDataSource
import com.mibrahimuadev.spending.data.model.Category
import com.mibrahimuadev.spending.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class CategoryRepository(application: Application) : CategoryDataSource {
    private val categoryDao: CategoryDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        categoryDao = database.categoryDao()
    }

    override suspend fun getAllCategoriesByType(typeCategory: TransactionType): Result<List<Category>> {
        return withContext(Dispatchers.IO) {
            Result.Success(categoryDao.getAllCategoriesByType(typeCategory))
        }
    }

    override suspend fun getDetailCategory(idKategori: Int): Result<Category?> {
        return withContext(Dispatchers.IO) {
            Result.Success(categoryDao.getDetailCategory(idKategori))
        }
    }

    suspend fun getLastCategoryId(): Result<Int> {
        return withContext(Dispatchers.IO) {
            Result.Success(categoryDao.getLastCategoryId())
        }
    }

    override suspend fun insertOrUpdateCategory(categoryEntity: CategoryEntity) {
        return withContext(Dispatchers.IO + NonCancellable) {
            categoryDao.insertOrUpdate(categoryEntity)
        }
    }

    override suspend fun insertCategory(categoryEntity: CategoryEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(categoryEntity: CategoryEntity) {
        TODO("Not yet implemented")
    }
}