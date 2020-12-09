package com.mibrahimuadev.spending.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.CurrentDate.getEndOfDay
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository = TransactionRepository(application)

    val _selectedYear = MutableLiveData<String>()
    val selectedYear: LiveData<String> = _selectedYear

    val _selectedMonth = MutableLiveData<String>()
    val selectedMonth: LiveData<String> = _selectedMonth

    val _selectedStartDate = MutableLiveData<String>()
    val selectedStartDate: LiveData<String> = _selectedStartDate

    val _selectedEndDate = MutableLiveData<String>()
    val selectedEndDate: LiveData<String> = _selectedEndDate

    val _allTransactions = MutableLiveData<List<TransactionList>>()
    val allTransactions: LiveData<List<TransactionList>> = _allTransactions

    init {
        Log.i("HomeViewModel", "HomeViewModel created")
    }

    private var isFirstLoaded: Boolean = true

    val _expenseNominal = MutableLiveData<String>()
    val expenseNominal: LiveData<String> = _expenseNominal

    val _incomeNominal = MutableLiveData<String>()
    val incomeNominal: LiveData<String> = _incomeNominal

    val _balanceNominal = MutableLiveData<String>()
    val balanceNominal: LiveData<String> = _balanceNominal

    fun onFirstLoaded() {
        if (isFirstLoaded) {
            isFirstLoaded = false
            _selectedYear.value = CurrentDate.year.toString()
            _selectedMonth.value = CurrentDate.month.plus(1).toString()
        }
    }

    fun displayData() {
        setDateRange()
        getSummaryTransaction()
        getAllTransactions()
    }

    private fun setDateRange() {
        val month = selectedMonth.value?.toInt()
        val formatDate = selectedYear.value + "-" + month.toString()
        _selectedEndDate.value = formatDate + "-" + getEndOfDay(formatDate) + " 23:59:59"
        _selectedStartDate.value = formatDate + "-01 00:00:00"
        Log.i("HomeViewModel", "${selectedStartDate.value} - ${selectedEndDate.value}")
    }

    private fun getSummaryTransaction() {
        viewModelScope.launch {
            val result =
                repository.getSummaryTransaction(selectedStartDate.value!!, selectedEndDate.value!!)
            if (result is Result.Success) {
                _expenseNominal.value = result.data.expenseNominal.toString()
                _incomeNominal.value = result.data.incomeNominal.toString()
                _balanceNominal.value = result.data.let {
                    it.incomeNominal - it.expenseNominal
                }.toString()
            }
        }
    }

    private fun getAllTransactions() {
        viewModelScope.launch {
            val result =
                repository.getAllTransactions(selectedStartDate.value!!, selectedEndDate.value!!)
            if (result is Result.Success) {
                _allTransactions.value = result.data
            }
        }
    }
}