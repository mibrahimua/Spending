package com.mibrahimuadev.spending.data.model

import java.util.*

data class SummaryTransaction(
    val rangeTransaction: Date,
    val expenseNominal: Double,
    val incomeNominal: Double,
    val balanceNominal: Double
)