package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setting_app")
data class SettingEntity (
    @PrimaryKey(autoGenerate = true)
    val settingId: Int,

    val userId: String?,

    val settingName: String,

    val settingValue: String?,

    val settingSysname: String,
)