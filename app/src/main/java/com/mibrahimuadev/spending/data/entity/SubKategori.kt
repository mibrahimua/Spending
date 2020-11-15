package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_kategori",
    )
data class SubKategori(
    @PrimaryKey(autoGenerate = true)
    val idSubKategori: Int,

    val idKategori: Int,

    val namaSubKategori: String?,

    val idIcon: Int?
)