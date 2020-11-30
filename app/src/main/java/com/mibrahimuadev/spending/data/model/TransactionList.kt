package com.mibrahimuadev.spending.data.model

import java.util.*

data class TransactionList(
    override val transactionId: Long,
    override val transactionDate: Date,
    override val transactionNominal: Double?,
    override val transactionType: TransactionType,
    override val transactionNote: String? = "",
    override val categoryId: Int,
    override val currencyId: String,
    val iconLocation: String,
    val categoryName: String? = "",
    val currencySymbol: String? = ""
) : BaseTransaction