package com.mibrahimuadev.spending.ui.icon

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.IconCategoryEntity
import com.mibrahimuadev.spending.data.repository.IconCategoryRepository
import com.mibrahimuadev.spending.utils.Result
import kotlinx.coroutines.launch

class IconCategoryViewModel(application: Application): AndroidViewModel(application) {
    private val iconCategoryRepository: IconCategoryRepository

    val allIconCategories = MutableLiveData<List<IconCategoryEntity>>()

    init {
        iconCategoryRepository = IconCategoryRepository(application)
        getAllIconCategories()
    }

    val _iconId = MutableLiveData<Int>()
    val iconId: LiveData<Int> = _iconId

    val _iconName = MutableLiveData<String>()
    val iconName: LiveData<String> = _iconName

    fun getAllIconCategories() {
        viewModelScope.launch {
            iconCategoryRepository.getAllIconCategories().let { result ->
                if(result is Result.Success) {
                   allIconCategories.value = result.data
                }
            }
        }
    }


//    fun getIconCategory(iconId: Int) {
//        viewModelScope.launch {
//            iconCategoryRepository.getDetailIconCategory(iconId).let { result->
//                if(result is Result.Success) {
//                    _iconId.value = result.data?.iconId
//                    _iconName.value = result.data?.iconName
//                }
//            }
//        }
//    }

}