package com.mibrahimuadev.spending.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransaksiKategori(
   @PrimaryKey(autoGenerate = true)
   val id: Long?,
   @ColumnInfo(name = "id_kategori")
   val idKategori: Int?,
   @ColumnInfo(name = "id_transaksi")
   val idTransaksi: Int?
)