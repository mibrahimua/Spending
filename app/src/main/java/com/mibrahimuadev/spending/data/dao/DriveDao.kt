package com.mibrahimuadev.spending.data.dao

import androidx.room.*
import com.mibrahimuadev.spending.data.entity.DriveEntity

@Dao
interface DriveDao {

    @Query("SELECT fileId FROM drive_entitiy WHERE fileType == 'folder'")
    suspend fun getFolderId(): String?

    @Query("SELECT fileId FROM drive_entitiy WHERE fileType == 'file'")
    suspend fun getFileId(): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFileDrive(driveEntity: DriveEntity): Long

    @Update
    suspend fun updateFileDrive(driveEntity: DriveEntity)

    @Query("DELETE FROM drive_entitiy")
    suspend fun deleteDrive()
}