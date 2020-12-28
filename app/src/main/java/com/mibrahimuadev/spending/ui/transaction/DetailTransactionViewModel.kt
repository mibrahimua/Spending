package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.utils.Event
import com.mibrahimuadev.spending.utils.Result
import kotlinx.coroutines.launch
import java.util.*

class DetailTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "TransactionViewModel"
    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository

    init {
        Log.i(TAG, "TransactionViewModel created")
        transactionRepository = TransactionRepository(application)
        categoryRepository = CategoryRepository(application)
    }

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    /**
     * Transaction Live Data
     */
    val _transactionId = MutableLiveData<Long>()
    var transactionId: LiveData<Long> = _transactionId

    val _transactionType = MutableLiveData<TransactionType>()
    val transactionType: LiveData<TransactionType> = _transactionType

    val _transactionNominal = MutableLiveData<String>()
    val transactionNominal: LiveData<String> = _transactionNominal

    val _categoryId = MutableLiveData<Int>()
    val categoryId: LiveData<Int> = _categoryId

    val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    val _dateTransaction = MutableLiveData<Date>()
    val dateTransaction: LiveData<Date> = _dateTransaction

    val _noteTransaction = MutableLiveData<String?>()
    val noteTransaction: LiveData<String?> = _noteTransaction


    fun getDetailTransaction(transactionId: Long) {
        viewModelScope.launch {
            val result = transactionRepository.getTransaction(transactionId)
            if (result is Result.Success) {
                _transactionType.value = result.data.transactionType
                _transactionNominal.value = result.data.transactionNominal.toString()
                _categoryName.value = result.data.categoryName
                _dateTransaction.value = result.data.transactionDate
                _noteTransaction.value = result.data.transactionNote
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("TransactionViewModel", "TransactionViewModel destroyed")
    }


}