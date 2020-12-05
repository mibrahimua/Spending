package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "TransactionViewModel"
    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository

    init {
        Log.i(TAG, "TransactionViewModel created")
        transactionRepository = TransactionRepository(application)
        categoryRepository = CategoryRepository(application)
    }

    /**
     * Variable navigation Args
     */
    var transactionIdArgs: Long = 0L
    var transactionTypeArgs: TransactionType? = null
    var categoryIdArgs: Int = 0
    var categoryNameArgs: String? = null

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val _transactionId = MutableLiveData<Long>()
    var transactionId: LiveData<Long> = _transactionId

    var isNewTransaction: Boolean = true

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
    val categoryId: LiveData<Int> = _categoryId

    val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    val _dateTransaction = MutableLiveData<Date>()
    val dateTransaction: LiveData<Date> = _dateTransaction

    val _noteTransaction = MutableLiveData<String?>()
    val noteTransaction: LiveData<String?> = _noteTransaction

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun startTransaction() {
        _dataLoading.value = true
        _transactionId.value = transactionIdArgs
        if (statusTransaction()) {
            onNewTransactionLoaded()
        } else {
            onExistTransactionLoaded()
        }
        onSelectedCategory()
    }

    fun statusTransaction(): Boolean {
        isNewTransaction = transactionId.value == 0L
        return isNewTransaction
    }

    private fun onNewTransactionLoaded() {
        _transactionType.value = transactionTypeArgs
        _categoryName.value = null
        _dataLoading.value = false
    }

    private fun onExistTransactionLoaded() {
        viewModelScope.launch {
            transactionRepository.getTransaction(transactionId.value!!).let { result ->
                if (result is Result.Success) {
                    withContext(Dispatchers.Main) {
                        _categoryId.value = result.data.categoryId
                        _categoryName.value = result.data.categoryName
                        _transactionType.value = result.data.transactionType
                        _transactionNominal.value = result.data.transactionNominal.toString()
                        _dateTransaction.value = result.data.transactionDate
                        _noteTransaction.value = result.data.transactionNote
                        _dataLoading.value = false
                    }
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onSelectedCategory() {
        if (categoryIdArgs != 0) {
            _categoryId.value = categoryIdArgs
            _categoryName.value = categoryNameArgs
        }
    }

    fun resetCategory() {
        _categoryId.value = null
        _categoryName.value = null
    }

    fun isCategoryExist(categoryId: Int) {
        viewModelScope.launch() {
            if (categoryId != 0) {
                categoryRepository.insertOrUpdateCategory(
                    Category(
                        categoryId = categoryId,
                        categoryName = categoryNameArgs,
                        iconId = 1,
                        typeCategory = transactionType.value!!
                    )
                )
            }
        }
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
                _categoryName.value = result.data.categoryName
                _dateTransaction.value = result.data.transactionDate
                _noteTransaction.value = result.data.transactionNote
            }
        }
    }

    fun validateTransaction() {
        _dataLoading.value = true
        val categoryId = categoryId.value ?: 0
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
        if (statusTransaction() || currentTransactionId == 0L) {
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

    override fun onCleared() {
        super.onCleared()
        Log.i("AddTransactionViewModel", "AddTransactionViewModel destroyed")
    }
}