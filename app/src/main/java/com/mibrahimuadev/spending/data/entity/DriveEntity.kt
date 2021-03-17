package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.BaseDrive
import java.util.*

@Entity(
    tableName = "drive_entitiy"
)
data class DriveEntity(
    @PrimaryKey(autoGenerate = false)
    override val fileType: String,
    override val fileId: String?,
    override val fileName: String?,
    override val lastModified: Date?
) : BaseDrive
