package com.mibrahimuadev.spending.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.CategoryEntity
import com.mibrahimuadev.spending.utils.Result
import com.mibrahimuadev.spending.data.model.Category
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository: CategoryRepository

    init {
        Log.i("CategoryViewModel", "CategoryViewModel created")
        categoryRepository = CategoryRepository(application)
    }

    private val _allCategories = MutableLiveData<List<Category>>()
    val allCategories: LiveData<List<Category>> = _allCategories

    val categoryName = MutableLiveData<String>()

    val _lastCategoryId = MutableLiveData<Int>()
    val lastCategoryId: LiveData<Int> = _lastCategoryId

    val _typeCategory = MutableLiveData<TransactionType>()
    val typeCategory: LiveData<TransactionType> = _typeCategory

    fun getAllCategoriesByType(typeCategory: TransactionType) {
        viewModelScope.launch() {
            val result = categoryRepository.getAllCategoriesByType(typeCategory)
            when (result) {
                is Result.Success -> _allCategories.value = result.data
            }
        }
    }

    fun getLastCategoryId() {
        viewModelScope.launch {
            categoryRepository.getLastCategoryId().let {
                if (it is Result.Success) {
                    _lastCategoryId.value = it.data
                }
            }
        }
    }

    fun getDetailCategory(idKategori: Int) {
        viewModelScope.launch {
            categoryRepository.getDetailCategory(idKategori).let { result ->
                if (result is Result.Success) {
                    categoryName.value = result.data?.categoryName
                }
            }
        }
    }

    fun insertOrUpdateCategory(category: Category) {
        viewModelScope.launch() {
            if (category.categoryId != 0) {
                categoryRepository.insertOrUpdateCategory(
                    CategoryEntity(
                        categoryId = category.categoryId,
                        categoryName = category.categoryName,
                        iconId = category.iconId!!,
                        categoryType = category.categoryType
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("CategoryViewModel", "CategoryViewModel destroyed")
    }
}