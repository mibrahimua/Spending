package com.mibrahimuadev.spending.utils

import androidx.room.TypeConverter
import com.mibrahimuadev.spending.data.model.BackupSchedule
import com.mibrahimuadev.spending.data.model.CategoryType
import com.mibrahimuadev.spending.data.model.TransactionType
import java.util.*

class ConvertersRoomDataType {
    @TypeConverter
    fun toTransactiontype(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactiontype(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toCategoryType(value: String): CategoryType {
        return CategoryType.valueOf(value)
    }

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String {
        return value.name
    }

    @TypeConverter
    fun toBackupScheduletype(value: String): BackupSchedule {
        return BackupSchedule.valueOf(value)
    }

    @TypeConverter
    fun fromBackupScheduletype(value: BackupSchedule): String {
        return value.name
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }


//    @TypeConverter
//    fun toDate(value: String?): Date? {
//        return value?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun toTimestamp(date: Date?): Long? {
//        return date?.time?.toLong()
//    }


}