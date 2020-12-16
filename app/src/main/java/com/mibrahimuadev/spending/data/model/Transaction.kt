package com.mibrahimuadev.spending.data.model

import java.util.*

data class Transaction(
    override val transactionId: Long,
    override val transactionDate: Date,
    override val transactionNominal: Double?,
    override val transactionType: TransactionType,
    override val transactionNote: String? = "",
    val categoryId: Int,
    val currencyId: String,
    val iconName: String,
    val categoryName: String? = "",
    val currencySymbol: String? = ""
) : BaseTransaction