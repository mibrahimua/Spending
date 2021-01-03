package com.mibrahimuadev.spending.data.model

import java.util.*

interface BaseTransaction {
    val transactionId: Long

    val transactionType: TransactionType

    val transactionIncome: Double?

    val transactionExpense: Double?

    val transactionDate: Date

    val transactionNote: String?
}