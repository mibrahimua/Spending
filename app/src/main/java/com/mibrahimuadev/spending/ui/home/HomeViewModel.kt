package com.mibrahimuadev.spending.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.SummaryTransaction
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository = TransactionRepository(application)

    val allTransaction: LiveData<Result<List<TransactionList>>> =
        repository.observeAllTransactions()

    init {
        Log.i("HomeViewModel", "HomeViewModel created")
    }

    val _expenseNominal = MutableLiveData<Double>()
    val expenseNominal: LiveData<Double> = _expenseNominal

    val _incomeNominal = MutableLiveData<Double>()
    val incomeNominal: LiveData<Double> = _incomeNominal

    val _balanceNominal = MutableLiveData<Double>()
    val balanceNominal: LiveData<Double> = _balanceNominal

    fun getSummaryTransaction(startDate: String, endDate: String) {
        viewModelScope.launch {
            val result = repository.getSummaryTransaction(startDate, endDate)
            if (result is Result.Success) {
                _expenseNominal.value = result.data.expenseNominal
                _incomeNominal.value = result.data.incomeNominal
                _balanceNominal.value = result.data.balanceNominal
            }
        }
    }

}