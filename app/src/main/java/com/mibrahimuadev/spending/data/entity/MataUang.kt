package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mata_uang")
data class MataUang(
    @PrimaryKey(autoGenerate = false)
    val idMataUang: String,

    val namaUang: String? = "",

    val symbolUang: String? = ""
)