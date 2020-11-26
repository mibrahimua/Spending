package com.mibrahimuadev.spending.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.Result.Error
import com.mibrahimuadev.spending.data.Result.Success
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.local.dao.CategoryDao
import com.mibrahimuadev.spending.data.model.CategoryDataSource
import com.mibrahimuadev.spending.data.model.CategoryList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryLocalDataSource internal constructor(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryDataSource {

    override fun observerAllCategories(): LiveData<Result<List<CategoryList>>> {
        return categoryDao.observeAllCategories().map {
            Success(it)
        }
    }

    override suspend fun getAllCategories(): Result<List<CategoryList>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(categoryDao.getAllCategories())
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getCategory(idKategori: Int): Result<CategoryList> {
        return withContext(ioDispatcher) {
            try {
                val category = categoryDao.getCategory(idKategori)
                if (category != null) {
                    return@withContext Success(category)
                } else {
                    return@withContext Error(Exception(""))
                }
            } catch (e: Exception) {
                return@withContext Error(e)
            }
        }
    }

    override suspend fun insertCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllCategories() {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(category: Category) {
        TODO("Not yet implemented")
    }

}