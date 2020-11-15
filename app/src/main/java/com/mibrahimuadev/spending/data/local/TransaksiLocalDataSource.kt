package com.mibrahimuadev.spending.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.Result.Error
import com.mibrahimuadev.spending.data.Result.Success
import com.mibrahimuadev.spending.data.entity.Transaksi
import com.mibrahimuadev.spending.data.local.dao.TransaksiDao
import com.mibrahimuadev.spending.data.model.ListTransaksi
import com.mibrahimuadev.spending.data.model.TransaksiDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransaksiLocalDataSource internal constructor(
    private val transaksiDao: TransaksiDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransaksiDataSource {

    override fun observeAllTransaksi(): LiveData<Result<List<ListTransaksi>>> {
        return transaksiDao.observeAllTransaksi().map {
            Success(it)
        }
    }

    override suspend fun getAllTransaksi(): Result<List<ListTransaksi>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(transaksiDao.getAllTransaksi())
            } catch (e: Exception) {
                Error(e)
            }
        }


    override suspend fun getTransaksi(): Result<List<ListTransaksi>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTransaksi(transaksi: Transaksi) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTransaksi() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransaksi(idTransaksi: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaksi(transaksi: Transaksi) {
        TODO("Not yet implemented")
    }
}