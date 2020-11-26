package com.mibrahimuadev.spending.data.model

import java.util.*

data class TransactionList(
    override val transactionId: Long,
    override val transactionDate: Date,
    val iconLocation: String,
    val categoryName: String? = "",
    val subCategoryName: String? = "",
    override val transactionNominal: Double,
    val currencySymbol: String? = "",
    override val transactionType: TransactionType,
    override val transactionNote: String? = ""
) : BaseTransaction