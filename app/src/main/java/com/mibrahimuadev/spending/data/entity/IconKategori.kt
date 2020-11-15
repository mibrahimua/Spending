package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_kategori")
data class IconKategori (
    @PrimaryKey(autoGenerate = true)
    val idIcon: Int,

    val namaIcon: String,

    val lokasiIcon: String
)