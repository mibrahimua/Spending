package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_category",
    )
data class SubCategory(
    @PrimaryKey(autoGenerate = true)
    val subCategoryId: Int,

    val categoryId: Int,

    val subCategoryName: String?,

    val iconId: Int?
)