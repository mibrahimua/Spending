package com.mibrahimuadev.spending.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.google.api.client.util.DateTime
import com.mibrahimuadev.spending.data.entity.BackupEntity
import java.util.*

@Dao
interface BackupDao {

    @Query("SELECT * FROM backup_entity WHERE userId = :userId")
    fun getBackupDate(userId: String? = ""): BackupEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBackupDate(backupEntity: BackupEntity): Long

    @Update
    fun updateBackupDate(backupEntity: BackupEntity)

    @Transaction
    suspend fun insertOrUpdateBackupDate(backupEntity: BackupEntity) {
        val id = insertBackupDate(backupEntity)
        if (id == -1L) updateBackupDate(backupEntity)
    }

    @Query("UPDATE backup_entity SET localBackup = :localBackup WHERE userId = :userId")
    suspend fun updateLocalBackup(userId: String, localBackup: String)

    @Query("UPDATE backup_entity SET googleBackup = :googleBackup WHERE userId = :userId")
    suspend fun updateGoogleBackup(userId: String, googleBackup: String)

    @Query("DELETE FROM backup_entity")
    fun deleteBackupDate()

    @RawQuery
    fun changeCheckpoint(supportSQLiteQuery: SupportSQLiteQuery?): Int
}