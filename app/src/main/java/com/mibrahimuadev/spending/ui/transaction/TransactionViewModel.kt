package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
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

    val _transactionId = MutableLiveData<Long>()
    var transactionId: LiveData<Long> = _transactionId

    private var isNewTransaction = MutableLiveData<Boolean>()

    val _transactionType = MutableLiveData<TransactionType>()
    val transactionType: LiveData<TransactionType> = _transactionType

    val _transactionNominal = MutableLiveData<String>()
    val transactionNominal: LiveData<String> = _transactionNominal

    val _calcLastKey = MutableLiveData<String>()
    val calcLastKey: LiveData<String> = _calcLastKey

    val _calcOperation = MutableLiveData<String>()
    val calcOperation: LiveData<String> = _calcOperation

    val _calcNewFormula = MutableLiveData<String>()
    val calcNewFormula: LiveData<String> = _calcNewFormula

    val _categoryId = MutableLiveData<Int>()
    val categoryId = _categoryId

    val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    val _dateTransaction = MutableLiveData<Date>()
    val dateTransaction: LiveData<Date> = _dateTransaction

    val _noteTransaction = MutableLiveData<String?>()
    val noteTransaction: LiveData<String?> = _noteTransaction

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun startTransaction(
        transactionIdArgs: Long,
        categoryIdArgs: Int,
        transactionTypeArgs: TransactionType
    ) {
        _transactionId.value = transactionIdArgs

        if (transactionId.value == 0L) {
            isNewTransaction.value = true
            _transactionType.value = transactionTypeArgs
            _categoryId.value = categoryIdArgs
            categoryId.value?.let { getCategory(it) }
            return
        } else {
            isNewTransaction.value = false
            _dataLoading.value = true
            viewModelScope.launch {
                transactionRepository.getTransaction(transactionIdArgs).let { result ->
                    if (result is Result.Success) {
                        withContext(Dispatchers.Main) {
                            onTransactionLoaded(result.data, categoryIdArgs)
                            categoryId.value?.let { getCategory(it) }
                        }
                    } else {
                        onDataNotAvailable()
                    }
                }
            }
        }
    }

    private fun onTransactionLoaded(transaction: TransactionList, categoryIdArgs: Int) {
        if (categoryIdArgs == 0) {
            _categoryId.value = transaction.categoryId
            _transactionType.value = transaction.transactionType
            _transactionNominal.value = transaction.transactionNominal.toString()
            _dateTransaction.value = transaction.transactionDate
            _noteTransaction.value = transaction.transactionNote
        } else {
            _categoryId.value = categoryIdArgs
        }
        _dataLoading.value = false
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun editTextNoteTransactionChanged(newText: String?) {
        if (newText == noteTransaction.value) {
            return
        } else {
            _noteTransaction.value = newText
        }
    }

    fun getDetailTransaction(transactionId: Long) {
        viewModelScope.launch {
            val result = transactionRepository.getTransaction(transactionId)
            if (result is Result.Success) {
                _transactionType.value = result.data.transactionType
                _transactionNominal.value = result.data.transactionNominal.toString()
                getCategory(result.data.categoryId)
                _dateTransaction.value = result.data.transactionDate
                _noteTransaction.value = result.data.transactionNote
            }
        }
    }

    fun validateTransaction() {
        _dataLoading.value = true
        val categoryId = categoryId.value!!
        if (categoryId == 0) {
            _errorMessage.value = "Category cannot empty"
            return
        }
        try {
            _errorMessage.value = ""
            saveTransaction()
            _navigateToHome.value = Event(true)
        } catch (e: Exception) {
            _errorMessage.value = "Invalid format numbers"
        }

    }

    fun saveTransaction() {
        val transactionNominal = transactionNominal.value!!.filterNot { it == ',' }.toDouble()
        val transactionType = transactionType.value!!
        val transactionCategory = categoryId.value!!
        val dateTransaction = dateTransaction.value!!
        val noteTransaction = noteTransaction.value
        val transactionCurrency = "IDR"
        Log.i(TAG, "call function saveTransaction")
        val currentTransactionId = transactionId.value
        if (isNewTransaction.value == true || currentTransactionId == 0L) {
            val newTransaction = Transaction(
                transactionNominal = transactionNominal,
                transactionType = transactionType,
                transactionDate = dateTransaction,
                categoryId = transactionCategory,
                currencyId = transactionCurrency,
                transactionNote = noteTransaction
            )
            createTransaction(newTransaction)
        } else {
            val updateTransaction = Transaction(
                transactionId = currentTransactionId!!,
                transactionNominal = transactionNominal,
                transactionType = transactionType,
                transactionDate = dateTransaction,
                categoryId = transactionCategory,
                currencyId = transactionCurrency,
                transactionNote = noteTransaction
            )
            updateTransaction(updateTransaction)
        }
    }

    private fun createTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
            _dataLoading.value = false
        }
    }

    private fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
            _dataLoading.value = false
        }
    }

    fun getCategory(idKategori: Int) {
        viewModelScope.launch {
            categoryRepository.getCategory(idKategori).let { result ->
                if (result is Result.Success) {
                    _categoryName.value = result.data?.categoryName
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("AddTransactionViewModel", "AddTransactionViewModel destroyed")
    }
}