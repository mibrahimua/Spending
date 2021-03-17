package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "backup_entity"
)
data class BackupEntity(
    @PrimaryKey
    val userId: String,

    val localBackup: String?,

    val googleBackup: String?,
)