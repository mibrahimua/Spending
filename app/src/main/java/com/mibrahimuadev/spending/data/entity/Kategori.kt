package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "kategori",
)
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    val idKategori: Int,

    val namaKategori: String? = "",

    val idIcon: Int

)