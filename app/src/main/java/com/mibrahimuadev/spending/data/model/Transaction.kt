package com.mibrahimuadev.spending.data.model

import java.util.*

data class Transaction(
    override val transactionId: Long,
    override val transactionDate: Date,
    override val transactionType: TransactionType,
    override val transactionExpense: Double,
    override val transactionIncome: Double,
    override val transactionNote: String? = "",
    val categoryId: Int,
    val iconName: String,
    val categoryName: String? = "",
) : BaseTransaction