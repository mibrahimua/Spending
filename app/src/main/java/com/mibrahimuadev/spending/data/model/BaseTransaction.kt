package com.mibrahimuadev.spending.data.model

import java.util.*

interface BaseTransaction {
    val transactionId: Long

    val transactionType: TransactionType

    val transactionNominal: Double?

    val transactionDate: Date

    val transactionNote: String?

    val categoryId: Int

    val currencyId: String

}