package com.mibrahimuadev.spending.data.model

import java.util.*

interface BaseDrive {
    val fileType: String
    val fileId: String?
    val fileName: String?
    val lastModified: Date?
}