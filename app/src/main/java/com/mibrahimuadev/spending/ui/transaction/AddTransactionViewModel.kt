package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.utils.Event
import kotlinx.coroutines.launch
import java.util.*

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AddTransactionViewModel"
    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository

    init {
        Log.i(TAG, "AddTransactionViewModel created")
        transactionRepository = TransactionRepository(application)
        categoryRepository = CategoryRepository(application)
    }

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val _transactionType = MutableLiveData<TransactionType>()
    val transactionType: LiveData<TransactionType> = _transactionType

    val _calcNewResult = MutableLiveData<String>()
    val calcNewResult: LiveData<String> = _calcNewResult

    val _calcNewFormula = MutableLiveData<String>()
    val calcNewFormula: LiveData<String> = _calcNewFormula

    val _transactionCategory = MutableLiveData<Int>()
    val transactionCategory = _transactionCategory

    val _dateTransaction = MutableLiveData<Date>()
    val dateTransaction: LiveData<Date> = _dateTransaction

    val _noteTransaction = MutableLiveData<String?>()
    val noteTransaction: LiveData<String?> = _noteTransaction

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun validateTransaction() {
        _dataLoading.value = true
        val operationsRegex = "[-+*/]".toPattern()
        val transactionNominal = calcNewResult.value!!
        val transactionCategory = transactionCategory.value!!
        if (transactionNominal.contains(operationsRegex.toRegex())) {
            _errorMessage.value = "Invalid format numbers"
            return
        } else if (transactionCategory == 0) {
            _errorMessage.value = "Category cannot empty"
            return
        } else {
            _errorMessage.value = ""
            saveTransaction()
            _navigateToHome.value = Event(true)
        }
    }

    fun saveTransaction() {
        val transactionNominal = calcNewResult.value!!.filterNot { it == ',' }.toDouble()
        val transactionType = transactionType.value!!
        val transactionCategory = transactionCategory.value!!
        val dateTransaction = dateTransaction.value!!
        val noteTransaction = noteTransaction.value
        val transactionCurrency = "IDR"
        Log.i(TAG, "call function saveTransaction")
        viewModelScope.launch() {
            Log.i(TAG, "coroutine starting")
            transactionRepository.insertTransaksi(
                Transaction(
                    transactionNominal = transactionNominal,
                    transactionType = transactionType,
                    transactionDate = dateTransaction,
                    categoryId = transactionCategory,
                    currencyId = transactionCurrency,
                    transactionNote = noteTransaction
                )
            )
            _dataLoading.value = false
            Log.i(TAG, "coroutine end")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("AddTransactionViewModel", "AddTransactionViewModel destroyed")
    }
}