package com.mibrahimuadev.spending.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mata_uang")
data class MataUang (
    @PrimaryKey(autoGenerate = false)
//    @ColumnInfo(name = "id_mata_uang")
    val idMataUang: Int,

//    @ColumnInfo(name = "nama_uang")
    val namaUang: String,

//    @ColumnInfo(name = "nama_uang_short")
    val namaUangShort: String
)