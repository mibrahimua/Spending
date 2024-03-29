package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_icon")
data class CategoryIcon (
    @PrimaryKey(autoGenerate = true)
    val iconId: Int,

    val iconName: String,

    val iconLocation: String
)