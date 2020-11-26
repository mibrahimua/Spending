package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.Result.Success
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var transactionResults: LiveData<List<Transaction>>

    private lateinit var allCategory: LiveData<List<Category>>

    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository


    init {
        Log.i("AddTransactionViewModel", "AddTransactionViewModel created")
        transactionRepository = TransactionRepository(application)
        categoryRepository = CategoryRepository(application)
    }


    fun insertTransaksi(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.insertTransaksi(transaction)
        }
    }

    val _calcNewResult = MutableLiveData<String>()
    val calcNewResult: LiveData<String> = _calcNewResult

    val _calcNewFormula = MutableLiveData<String>()
    val calcNewFormula: LiveData<String> = _calcNewFormula

    val categoryName = MutableLiveData<String>()


    fun getCategory(idKategori: Int){
        viewModelScope.launch {
            categoryRepository.getCategory(idKategori).let { result ->
                if (result is Success) {
                    categoryName.value = result.data.categoryName
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("AddTransactionViewModel", "AddTransactionViewModel destroyed")
    }
}