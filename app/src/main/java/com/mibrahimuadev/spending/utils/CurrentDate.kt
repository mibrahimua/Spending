package com.mibrahimuadev.spending.utils

import java.text.SimpleDateFormat
import java.util.*

object CurrentDate {
    val now = GregorianCalendar()
    val year: Int = now.get(Calendar.YEAR)
    val month: Int = now.get(Calendar.MONTH)
    val day: Int = now.get(Calendar.DAY_OF_MONTH)
    val monthName = listOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    )

    fun dateFormat(formatDate: String): SimpleDateFormat {
        return SimpleDateFormat(formatDate, Locale.getDefault())
    }

    fun getCustomDate(specificDate: Date, typeDate: String): Int {
        now.time = specificDate
        when (typeDate) {
            "dd" -> return now.get(Calendar.DAY_OF_MONTH)
            "MM" -> return now.get(Calendar.MONTH)
            "yyyy" -> return now.get(Calendar.YEAR)
            else -> return 0
        }
    }

    fun getDateString(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }


    fun getCurrentDate(formatDate: String): Date? {
        val initialDate = dateFormat(formatDate)
            .parse("${day} ${month} ${year}")
        return initialDate
    }
}