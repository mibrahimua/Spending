package com.mibrahimuadev.spending.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_transaksi")
data class IconTransaksi (
    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id_icon")
    val idIcon: Int,

//    @ColumnInfo(name = "nama_icon")
    val namaIcon: String,

//    @ColumnInfo(name = "lokasi_icon")
    val lokasiIcon: String
)