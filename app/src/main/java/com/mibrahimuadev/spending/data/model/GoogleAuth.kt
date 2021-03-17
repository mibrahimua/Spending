package com.mibrahimuadev.spending.data.model

interface GoogleAuth {
    val userId: Int
    val accessToken: String
    val refreshToken: String
}

