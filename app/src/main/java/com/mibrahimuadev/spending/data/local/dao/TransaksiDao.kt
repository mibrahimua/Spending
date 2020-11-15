package com.mibrahimuadev.spending.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.entity.Transaksi
import com.mibrahimuadev.spending.data.model.ListTransaksi

@Dao
interface TransaksiDao {

    @Query(
        """SELECT t.idTransaksi, t.tipeTransaksi, t.nominalTransaksi, t.tglTransaksi, k.namaKategori, sk.namaSubKategori, c.lokasiIcon, m.symbolUang , t.catatanTransaksi
                FROM transaksi t
                LEFT JOIN kategori k ON t.idSubKategori = k.idKategori
				LEFT JOIN sub_kategori sk on sk.idKategori = k.idKategori
                LEFT JOIN icon_kategori c ON c.idIcon = k.idIcon
                LEFT JOIN mata_uang m ON m.idMataUang = t.idMataUang
				WHERE t.tipeTransaksi = "PENGELUARAN"
				GROUP BY t.idTransaksi"""
    )
    fun observeAllTransaksi(): LiveData<List<ListTransaksi>>

    @Query(
        """SELECT t.idTransaksi, t.tipeTransaksi, t.nominalTransaksi, t.tglTransaksi, k.namaKategori, sk.namaSubKategori, c.lokasiIcon, m.symbolUang, t.catatanTransaksi 
                FROM transaksi t
                LEFT JOIN kategori k ON t.idSubKategori = k.idKategori
				LEFT JOIN sub_kategori sk on sk.idKategori = k.idKategori
                LEFT JOIN icon_kategori c ON c.idIcon = k.idIcon
                LEFT JOIN mata_uang m ON m.idMataUang = t.idMataUang
				WHERE t.tipeTransaksi = "PENGELUARAN"
				GROUP BY t.idTransaksi"""
    )
    suspend fun getAllTransaksi(): List<ListTransaksi>

    @Insert
    suspend fun insertTransaksi(transaksi: Transaksi)

    @Update
    suspend fun updateTransaksi(transaksi: Transaksi)

    //    @Delete
    @Query("DELETE FROM transaksi")
    suspend fun deleteAllTransaksi()

    @Query("DELETE FROM transaksi WHERE idTransaksi = :idTransaksi")
    suspend fun deleteTransaksi(idTransaksi: Long)
}