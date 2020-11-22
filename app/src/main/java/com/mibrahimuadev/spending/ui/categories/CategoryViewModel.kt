package com.mibrahimuadev.spending.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.CategoryList
import com.mibrahimuadev.spending.data.repository.CategoryRepository

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository: CategoryRepository

    init {
        Log.i("CategoryViewModel", "CategoryViewModel created")
        categoryRepository = CategoryRepository(application)
    }

    val allCategories = categoryRepository.observeAllCategories()
}