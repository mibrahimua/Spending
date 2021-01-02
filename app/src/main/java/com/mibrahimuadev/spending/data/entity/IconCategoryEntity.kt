package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_category")
data class IconCategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val iconId: Int,

    val iconName: String,
)