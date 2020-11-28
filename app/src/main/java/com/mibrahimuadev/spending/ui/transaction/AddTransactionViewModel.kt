package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.repository.CategoryRepository
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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

    fun saveTransaction() {
        val transactionNominal = calcNewResult.value!!.filterNot { it == ',' }.toDouble()

        val transactionType = transactionType.value!!
        val transactionCategory = transactionCategory.value!!
        val dateTransaction = dateTransaction.value!!
        val noteTransaction = noteTransaction.value
        val transactionCurrency = "IDR"
        insertTransaksi(
            Transaction(
                transactionNominal = transactionNominal,
                transactionType = transactionType,
                transactionDate = dateTransaction,
                categoryId = transactionCategory,
                currencyId = transactionCurrency,
                transactionNote = noteTransaction
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("AddTransactionViewModel", "AddTransactionViewModel destroyed")
    }
}