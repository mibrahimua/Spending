package com.mibrahimuadev.spending.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.CategoryList
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository: CategoryRepository
    private val _allCategories = MutableLiveData<List<CategoryList>>()
    val allCategories: LiveData<List<CategoryList>> = _allCategories
    val categoryName = MutableLiveData<String>()

    init {
        Log.i("CategoryViewModel", "CategoryViewModel created")
        categoryRepository = CategoryRepository(application)
    }

    val _lastCategoryId = MutableLiveData<Int>()
    val lastCategoryId: LiveData<Int> = _lastCategoryId

    fun getLastCategoryId() {
        viewModelScope.launch {
            categoryRepository.getLastCategoryId().let {
                if(it is Result.Success) {
                    _lastCategoryId.value = it.data
                }
            }
        }
    }

    fun getAllCategories(typeCategory: TransactionType) {
        viewModelScope.launch() {
            val result = categoryRepository.getAllCategories(typeCategory)
            when (result) {
                is Result.Success -> _allCategories.value = result.data
            }
        }
    }


    fun getCategory(idKategori: Int) {
        viewModelScope.launch {
            categoryRepository.getCategory(idKategori).let { result ->
                if (result is Result.Success) {
                    categoryName.value = result.data?.categoryName
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("CategoryViewModel", "CategoryViewModel destroyed")
    }
}