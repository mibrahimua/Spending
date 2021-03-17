package com.mibrahimuadev.spending.utils

import androidx.room.TypeConverter
import com.mibrahimuadev.spending.data.model.TransactionType
import java.util.*

class Converters {
    @TypeConverter
    fun toTransactiontype(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactiontype(value: TransactionType): String {
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