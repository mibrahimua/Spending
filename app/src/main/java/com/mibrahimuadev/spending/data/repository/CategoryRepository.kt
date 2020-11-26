package com.mibrahimuadev.spending.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.Result.Error
import com.mibrahimuadev.spending.data.Result.Success
import com.mibrahimuadev.spending.data.local.CategoryLocalDataSource
import com.mibrahimuadev.spending.data.model.CategoryList

class CategoryRepository(application: Application) {
    private val categoryLocalDataSource: CategoryLocalDataSource

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
    }

    fun observeAllCategories(): LiveData<Result<List<CategoryList>>> {
        return categoryLocalDataSource.observerAllCategories()
    }

    suspend fun getAllCategories(): Result<List<CategoryList>> {
        return categoryLocalDataSource.getAllCategories()
    }

    suspend fun getCategory(idKategori: Int): Result<CategoryList>{
        return categoryLocalDataSource.getCategory(idKategori)
    }
}