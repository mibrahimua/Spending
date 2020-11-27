package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.TransactionType


@Entity(
    tableName = "category",
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int,

    val categoryName: String? = "",

    val iconId: Int,

    val typeCategory: TransactionType

)