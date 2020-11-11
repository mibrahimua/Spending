package com.mibrahimuadev.spending.database.entity

import androidx.room.TypeConverter
import com.mibrahimuadev.spending.database.model.TipeTransaksi
import java.util.*

class Converters {
    @TypeConverter
    fun toTipeTransaksi(value: String): TipeTransaksi {
        return TipeTransaksi.valueOf(value)
    }

    @TypeConverter
    fun fromTipeTransaksi(value: TipeTransaksi): String {
        return value.name
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}