package com.mibrahimuadev.spending.data.backup

interface CreateJsonDbVersion {

    suspend fun fetchData()

    fun writeJSONToFile(): String
}