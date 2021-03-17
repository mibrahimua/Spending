package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.GoogleAuth

@Entity(
    tableName = "account",
)
data class AccountEntity(
    @PrimaryKey
    val userId: String,

    val userName: String,

    val userEmail: String,
)