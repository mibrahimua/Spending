package com.mibrahimuadev.spending.data.repository

import android.app.Application
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.dao.IconCategoryDao
import com.mibrahimuadev.spending.data.entity.IconCategoryEntity
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IconCategoryRepository(application: Application) {
    private val iconCategoryDao: IconCategoryDao

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        iconCategoryDao = database.iconCategoryDao()
    }

    suspend fun getAllIconCategories(): Result<List<IconCategoryEntity>> {
        return withContext(Dispatchers.IO) {
            Result.Success(iconCategoryDao.getAllIcons())
        }
    }

    suspend fun getDetailIconCategory(iconId: Int): Result<IconCategoryEntity> {
        return withContext(Dispatchers.IO) {
            Result.Success(iconCategoryDao.getDetailIcon(iconId))
        }
    }

    suspend fun getDetailIconCategoryByName(iconName: String): Result<IconCategoryEntity> {
        return withContext(Dispatchers.IO) {
            Result.Success(iconCategoryDao.getDetailIconByName(iconName))
        }
    }
}