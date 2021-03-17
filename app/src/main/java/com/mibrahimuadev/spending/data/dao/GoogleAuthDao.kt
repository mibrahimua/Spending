package com.mibrahimuadev.spending.data.dao

import androidx.room.*
import com.mibrahimuadev.spending.data.entity.GoogleAuthEntity

@Dao
interface GoogleAuthDao {
    @Query("SELECT * FROM google_auth WHERE userId = :userId")
    fun getGoogleAuth(userId: Int): GoogleAuthEntity?


    @androidx.room.Transaction
    suspend fun insertOrUpdate(googleAuthEntity: GoogleAuthEntity) {
        val id = insertGoogleAuth(googleAuthEntity)
        if (id == -1L) updateGoogleAuth(googleAuthEntity)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoogleAuth(googleAuthEntity: GoogleAuthEntity): Long

    @Update
    suspend fun updateGoogleAuth(googleAuthEntity: GoogleAuthEntity)

    @Query("DELETE FROM google_auth")
    suspend fun deleteGoogleAuth()
}