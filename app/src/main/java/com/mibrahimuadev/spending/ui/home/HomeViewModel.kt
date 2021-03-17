package com.mibrahimuadev.spending.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import com.mibrahimuadev.spending.data.source.TransactionRepositoryInterface
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.CurrentDate.getEndOfDay
import com.mibrahimuadev.spending.utils.wrapper.Result
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepositoryInterface = TransactionRepository(application)

    val _selectedYear = MutableLiveData<String>()
    val selectedYear: LiveData<String> = _selectedYear

    val _selectedMonth = MutableLiveData<String>()
    val selectedMonth: LiveData<String> = _selectedMonth

//    val _currentSelectedYear = MutableLiveData<String>()
//    val currentSelectedYear: LiveData<String> = _currentSelectedYear

    val _selectedStartDate = MutableLiveData<String>()
    val selectedStartDate: LiveData<String> = _selectedStartDate

    val _selectedEndDate = MutableLiveData<String>()
    val selectedEndDate: LiveData<String> = _selectedEndDate

    val _allTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions

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

    val _previousBalanceNominal = MutableLiveData<String>()
    val previousBalanceNominal: LiveData<String> = _previousBalanceNominal

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
        val formatDate = selectedYear.value + "-" + month?.let { padStartMonth(it) }
        _selectedEndDate.value = formatDate + "-" + getEndOfDay(formatDate) + " 23:59:59"
        _selectedStartDate.value = formatDate + "-01 00:00:00"
        Log.i("HomeViewModel", "${selectedStartDate.value} - ${selectedEndDate.value}")
    }

    private fun padStartMonth(month: Int): String {
        val newMonth = month.let {
            if (it < 10) {
                it.toString().padStart(2, '0')
            } else {
                it.toString()
            }
        }
        return newMonth
    }

    private fun getSummaryTransaction() {
        viewModelScope.launch {
            val job1 = viewModelScope.launch {
                getPreviousSummaryTransaction()
            }

            job1.join()
            val result =
                repository.getSummaryTransaction(
                    selectedStartDate.value!!,
                    selectedEndDate.value!!
                )
            if (result is Result.Success) {
                _expenseNominal.value = result.data.expenseNominal.toString()
                _incomeNominal.value = result.data.incomeNominal.toString()
                _balanceNominal.value = result.data.let {
                    (it.incomeNominal - it.expenseNominal).plus(previousBalanceNominal.value!!.toInt())
                }.toString()
            }
        }
    }

    private suspend fun getPreviousSummaryTransaction() {
        var month = selectedMonth.value?.toInt()
        var year = selectedYear.value?.toInt()
        var getEndOfMonthDay = true
        if (month?.equals(1) == true) {
            getEndOfMonthDay = false
        } else {
            month = month?.minus(1)
        }

        val formatDate = year.toString() + "-" + month?.let { padStartMonth(it) }
        val startDate = "$year-01-01 00:00:00"
        val endDate = getEndOfMonthDay.let {
            if (it) {
                formatDate + "-" + getEndOfDay(formatDate) + " 23:59:59"
            } else {
                "$year-01-01 00:00:01"
            }
        }

        val result = repository.getPreviousSummaryTransaction(startDate, endDate)
        if (result is Result.Success) {
            _previousBalanceNominal.value = result.data.previousBalanceNominal.toString()
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