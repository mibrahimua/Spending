package com.mibrahimuadev.spending.ui.backup

import com.mibrahimuadev.spending.data.model.BaseDrive
import java.util.*

data class DriveData(
    override val fileType: String,
    override val fileId: String?,
    override val fileName: String?,
    override val lastModified: Date?
): BaseDrive