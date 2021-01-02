package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "account",
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val userName: String,

    val userEmail: String
)