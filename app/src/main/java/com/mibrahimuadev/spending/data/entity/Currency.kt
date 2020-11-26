package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey(autoGenerate = false)
    val currencyId: String,

    val currencyName: String? = "",

    val currencySymbol: String? = ""
)