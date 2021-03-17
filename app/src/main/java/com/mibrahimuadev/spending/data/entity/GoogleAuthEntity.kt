package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.GoogleAuth

@Entity(
    tableName = "google_auth",
)
data class GoogleAuthEntity(
    @PrimaryKey(autoGenerate = false)
    override val userId: Int,
    override val accessToken: String,
    override val refreshToken: String
) : GoogleAuth