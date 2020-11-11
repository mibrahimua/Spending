package com.mibrahimuadev.spending.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mibrahimuadev.spending.database.entity.Transaksi
import com.mibrahimuadev.spending.database.entity.TransaksiWithKategori

@Dao
interface TransaksiDao {
    @Transaction
    @Query(
        "SELECT t.idTransaksi, t.tipeTransaksi, t.nominal, t.idKategori, k.namaKategori, c.idIcon, c.namaIcon, c.lokasiIcon, m.idMataUang, m.namaUang, m.namaUangShort FROM transaksi t LEFT JOIN kategori k ON t . idKategori = k . idKategori LEFT JOIN icon_transaksi c ON c.idIcon = k.idIcon LEFT JOIN mata_uang m ON m.idMataUang = t.idMataUang"
    )
//    @Query("SELECT * FROM transaksi")
    fun lihatSemuaTransaksi(): LiveData<List<TransaksiWithKategori>>

//    @Query("SELECT * FROM transaksi LIMIT 1")
//    fun lihatDetailTransaksi(): LiveData<Transaksi>
//
//    @Query("SELECT EXISTS(SELECT id_transaksi FROM transaksi LIMIT 1)")
//    fun cekTransaksi(): Boolean

    @Insert
    suspend fun insertTransaksi(transaksi: Transaksi)

    @Update
    suspend fun updateTransaksi(transaksi: Transaksi)

    //    @Delete
    @Query("DELETE FROM transaksi")
    suspend fun deleteAllTransaksi()
}