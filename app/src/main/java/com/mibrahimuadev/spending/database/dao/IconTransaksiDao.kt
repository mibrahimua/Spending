package com.mibrahimuadev.spending.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mibrahimuadev.spending.database.entity.IconTransaksi
import com.mibrahimuadev.spending.database.entity.Kategori

@Dao
interface IconTransaksiDao {
    @Query("SELECT * FROM icon_transaksi")
    fun lihatSemuaIcon(): LiveData<List<IconTransaksi>>

    @Insert
    suspend fun insertIcon(iconTransaksi: IconTransaksi)

    @Query("DELETE FROM icon_transaksi")
    suspend fun deleteAllIcon()
}