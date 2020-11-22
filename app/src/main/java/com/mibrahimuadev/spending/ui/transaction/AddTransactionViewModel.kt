package com.mibrahimuadev.spending.ui.transaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mibrahimuadev.spending.data.entity.Category
import com.mibrahimuadev.spending.data.entity.Transaction
import com.mibrahimuadev.spending.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var transactionResults: LiveData<List<Transaction>>

    private lateinit var allCategory: LiveData<List<Category>>

    private val transactionRepository: TransactionRepository
//    private val kategoriRepository: KategoriRepository


    init {
        Log.i("PemasukanViewModel", "PemasukanViewModel created")
        transactionRepository = TransactionRepository(application)
    }

    private val _navigateFromHome = MutableLiveData<Boolean?>()

    fun insertTransaksi(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.insertTransaksi(transaction)
        }
    }

    val _dataCalculator = MutableLiveData<Double>()
    val dataCalculator: LiveData<Double> = _dataCalculator

//    fun lihatSemuaKategori():LiveData<List<Kategori>> {
//        return kategoriRepository.allKategori
//    }

    val navigateFromHome: LiveData<Boolean?>
        get() = _navigateFromHome

    override fun onCleared() {
        super.onCleared()
        Log.i("PemasukanViewModel", "PemasukanViewModel destroyed")
    }
}