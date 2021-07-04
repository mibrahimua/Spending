package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.TransactionEntity
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.data.source.TransactionRepositoryInterface
import com.mibrahimuadev.spending.utils.Formatter
import com.mibrahimuadev.spending.utils.format
import com.mibrahimuadev.spending.utils.wrapper.Event
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "TransactionViewModel"
    private val transactionRepository: TransactionRepositoryInterface
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

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val _transactionType = MutableLiveData<TransactionType>()
    val transactionType: LiveData<TransactionType> = _transactionType

    val _transactionNominal = MutableLiveData<String>()
    val transactionNominal: LiveData<String> = _transactionNominal

    /**
     * Calculator Live Data
     */
    val _calcLastKey = MutableLiveData<String>()
    val calcLastKey: LiveData<String> = _calcLastKey

    val _calcOperation = MutableLiveData<String>()
    val calcOperation: LiveData<String> = _calcOperation

    val _calcNewFormula = MutableLiveData<String>()
    val calcNewFormula: LiveData<String> = _calcNewFormula

    /**
     * Transaction Live Data
     */
    val _transactionId = MutableLiveData<Long>()
    var transactionId: LiveData<Long> = _transactionId

    val _transactionIncome = MutableLiveData<String>()
    val transactionIncome: LiveData<String> = _transactionIncome

    val _transactionExpense = MutableLiveData<String>()
    val transactionExpense: LiveData<String> = _transactionExpense

    val _categoryId = MutableLiveData<Int?>()
    val categoryId: LiveData<Int?> = _categoryId

    val _categoryName = MutableLiveData<String?>()
    val categoryName: LiveData<String?> = _categoryName

    val _dateTransaction = MutableLiveData<Date>()
    val dateTransaction: LiveData<Date> = _dateTransaction

    val _noteTransaction = MutableLiveData<String?>()
    val noteTransaction: LiveData<String?> = _noteTransaction

    fun displayDataTransaction() {
        _dataLoading.value = true
        if (transactionIdArgs != 0L) {
            getDetailTransaction(transactionIdArgs)
        } else {
            _transactionType.value = transactionTypeArgs
            _dataLoading.value = false
        }
    }

    fun getDetailTransaction(transactionId: Long) {
        viewModelScope.launch {
            val job1 = viewModelScope.launch {
                val result = transactionRepository.getTransaction(transactionId)
                if (result is Result.Success) {
                    _transactionId.value = result.data.transactionId
                    _transactionType.value = result.data.transactionType
                    _transactionIncome.value = result.data.transactionIncome.format()
                    _transactionExpense.value = result.data.transactionExpense.format()
                    _categoryId.value = result.data.categoryId
                    _categoryName.value = result.data.categoryName!!
                    _dateTransaction.value = result.data.transactionDate
                    _noteTransaction.value = result.data.transactionNote
                    _dataLoading.value = false
                }
            }
            job1.join()
            convertToTransactionNominal()
        }
    }

    fun convertToTransactionNominal() {
        if (transactionType.value?.equals(TransactionType.INCOME) == true) {
            _transactionNominal.value = transactionIncome.value
        } else if (transactionType.value?.equals(TransactionType.EXPENSE) == true) {
            _transactionNominal.value = transactionExpense.value
        }
    }

    fun convertFromTransactionNominal() {
        if (transactionType.value?.equals(TransactionType.INCOME) == true) {
            _transactionIncome.value = _transactionNominal.value
            _transactionExpense.value = "0"
        } else if (transactionType.value?.equals(TransactionType.EXPENSE) == true) {
            _transactionExpense.value = _transactionNominal.value
            _transactionIncome.value = "0"
        }
    }

    fun resetCategory() {
        _categoryId.value = null
        _categoryName.value = null
    }

    fun saveCalculatorState(lastOperation: String, lastKey: String) {
        _calcOperation.value = lastOperation
        _calcLastKey.value = lastKey
        /**
         * saat pertama kali membuka halaman tambah transaksi
         * jika user menekan operator pertama kali maka saat submit operator tersebut tidak hilang
         * bahkan jika diclear
         */
    }

    fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun editTextNoteTransactionChanged(newText: String?) {
        if (newText == noteTransaction.value) {
            return
        } else {
            _noteTransaction.value = newText
        }
    }

    fun validateTransaction() {
        _dataLoading.value = true
        convertFromTransactionNominal()
        val categoryId = categoryId.value ?: 0
        if (categoryId == 0) {
            _errorMessage.value = "Category cannot empty"
            _dataLoading.value = false
            return
        }
        try {
            _errorMessage.value = ""
            saveTransaction()
            _navigateToHome.value = Event(true)
        } catch (e: Exception) {
            Log.i(TAG, "$e")
            _errorMessage.value = "Invalid format numbers"
        }
    }

    fun saveTransaction() {
        val transactionType = transactionType.value!!
        val transactionIncome = transactionIncome.value!!.filterNot { it == ',' }.toDouble()
        val transactionExpense = transactionExpense.value!!.filterNot { it == ',' }.toDouble()
        val transactionCategory = categoryId.value!!
        val dateTransaction = dateTransaction.value!!
        val noteTransaction = noteTransaction.value
        val currentTransactionId = transactionId.value
        val dataTransaction = TransactionEntity(
            transactionId = currentTransactionId ?: 0L,
            transactionType = transactionType,
            transactionIncome = transactionIncome,
            transactionExpense = transactionExpense,
            transactionDate = dateTransaction,
            categoryId = transactionCategory,
            transactionNote = noteTransaction
        )
        viewModelScope.launch {
            transactionRepository.insertOrUpdateTransaction(dataTransaction)
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
            _navigateToHome.value = Event(true)
            _dataLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("TransactionViewModel", "TransactionViewModel destroyed")
    }


}