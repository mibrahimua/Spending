package com.mibrahimuadev.spending.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object Formatter {
    fun doubleToString(d: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = ','

        val formatter = DecimalFormat()
        formatter.maximumFractionDigits = 12
        formatter.decimalFormatSymbols = symbols
        formatter.isGroupingUsed = true
        return formatter.format(d)
    }

    fun stringToDouble(str: String) = str.replace(",", "").toDouble()

    fun addGroupingSeparators(str: String) = doubleToString(stringToDouble(str))

    fun addThousandsDelimiter(nominalTransaction: String): String {
        var convertNominal = ""
        val numbersRegex = "[^0-9,.]".toRegex()
        val valueToCheck =
            numbersRegex.split(nominalTransaction).filter { it.trim().isNotEmpty() }
        valueToCheck.forEach {
            var newString = addGroupingSeparators(it)
            // allow writing numbers like 0.0003 but not allow if number only like 0.0
            if (it.contains(".") && it.substringAfter(".") != "0") {
                newString = newString.substringBefore(".") + ".${it.substringAfter(".")}"
            }
            convertNominal = nominalTransaction.replace(it, newString)
        }
        return convertNominal
    }
}

fun Double.format(): String = Formatter.doubleToString(this)