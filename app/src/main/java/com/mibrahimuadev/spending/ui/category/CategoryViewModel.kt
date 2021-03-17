package com.mibrahimuadev.spending.ui.category

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.CategoryEntity
import com.mibrahimuadev.spending.data.model.Category
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.IconCategoryRepository
import com.mibrahimuadev.spending.utils.wrapper.Event
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryRepository: CategoryRepository
    private val iconCategoryRepository: IconCategoryRepository

    init {
        Log.i("CategoryViewModel", "CategoryViewModel created")
        categoryRepository = CategoryRepository(application)
        iconCategoryRepository = IconCategoryRepository(application)
    }

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _navigateToCategorySetting = MutableLiveData<Event<Boolean>>()
    val navigateToCategorySetting: LiveData<Event<Boolean>>
        get() = _navigateToCategorySetting

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _allCategories = MutableLiveData<List<Category>>()
    val allCategories: LiveData<List<Category>> = _allCategories

    val _categoryId = MutableLiveData<Int>()
    val categoryId: LiveData<Int> = _categoryId

    val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    val _iconId = MutableLiveData<Int>()
    val iconId: LiveData<Int> = _iconId

    val _iconName = MutableLiveData<String>()
    val iconName: LiveData<String> = _iconName

    val _categoryType = MutableLiveData<TransactionType>()
    val categoryType: LiveData<TransactionType> = _categoryType

    val _lastCategoryId = MutableLiveData<Int>()
    val lastCategoryId: LiveData<Int> = _lastCategoryId

    fun getAllCategoriesByType(typeCategory: TransactionType) {
        _dataLoading.value = true
        viewModelScope.launch() {
            val result = categoryRepository.getAllCategoriesByType(typeCategory)
            when (result) {
                is Result.Success -> _allCategories.value = result.data
            }
            _dataLoading.value = false
        }
    }

    fun getDetailCategory(idKategori: Int) {
        if (idKategori != 0) {
            _dataLoading.value = true
            viewModelScope.launch {
                categoryRepository.getDetailCategory(idKategori).let { result ->
                    if (result is Result.Success) {
                        _categoryId.value = result.data?.categoryId
                        _categoryName.value = result.data?.categoryName
                        _iconId.value = result.data?.iconId
                        _iconName.value = result.data?.iconName
                        _categoryType.value = result.data?.categoryType
                    }
                }
                _dataLoading.value = false
            }
        }
    }

    fun getIconCategoryByName(iconName: String) {
        _dataLoading.value = true
        viewModelScope.launch {
            iconCategoryRepository.getDetailIconCategoryByName(iconName).let { result ->
                if (result is Result.Success) {
                    _iconId.value = result.data?.iconId
                    _iconName.value = result.data?.iconName
                }
            }
            _dataLoading.value = false
        }
    }

    fun getLastCategoryId() {
        viewModelScope.launch {
            categoryRepository.getLastCategoryId().let {
                if (it is Result.Success) {
                    _lastCategoryId.value = it.data
                    _categoryId.value = it.data
                }
            }
        }
    }

    fun validateCategory() {
        _dataLoading.value = true
        if (categoryName.value.isNullOrEmpty()) {
            _errorMessage.value = "Category name cannot empty"
            _dataLoading.value = false
            return
        }
        if(iconName.value.isNullOrEmpty()) {
            _errorMessage.value = "Icon category cannot empty"
            _dataLoading.value = false
            return
        }
        try {
            _errorMessage.value = ""
            insertOrUpdateCategory()
            _navigateToCategorySetting.value = Event(true)
        } catch (e: Exception) {
            _errorMessage.value = "Can't saving category"
        }
    }

    fun insertOrUpdateCategory() {
        val categoryId = categoryId.value ?: 0
        viewModelScope.launch() {
            if (categoryId != 0) {
                val job = viewModelScope.launch {
                    getLastCategoryId()
                }
                job.join()
            }
            categoryRepository.insertOrUpdateCategory(
                CategoryEntity(
                    categoryId = categoryId,
                    categoryName = categoryName.value!!,
                    iconId = iconId.value!!,
                    categoryType = categoryType.value!!
                )
            )
        }
    }

    fun editTextCategoryChanged(newText: String?) {
        if (newText == categoryName.value) {
            return
        } else {
            _categoryName.value = newText
        }
    }

    fun clearLiveDataCategory() {
        _categoryId.value = null
        _categoryName.value = ""
        _iconId.value = null
        _iconName.value = ""
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("CategoryViewModel", "CategoryViewModel destroyed")
    }
}