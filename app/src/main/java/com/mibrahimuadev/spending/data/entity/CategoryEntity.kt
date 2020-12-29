package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.TransactionType


@Entity(
    tableName = "category",
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    val categoryName: String? = "",

    val iconId: Int,
//nama variable tidak sesuai
    val categoryType: TransactionType

)