package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.BaseTransaction
import com.mibrahimuadev.spending.data.model.TransactionType
import java.util.*

@Entity(
    tableName = "transaction_spend",
//    foreignKeys = arrayOf(
//        ForeignKey(
//            entity = MataUang::class,
//            parentColumns = arrayOf("idMataUang"),
//            childColumns = arrayOf("idMataUang"),
//            onDelete = ForeignKey.CASCADE
//        )
//    )
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    override val transactionId: Long,

    override val transactionType: TransactionType,

    val subCategoryId: Int,

    val currencyId: String,

    override val transactionNominal: Double,

    override val transactionDate: Date,

    override val transactionNote: String? = ""

) : BaseTransaction {
    override fun toString(): String {
        return "Transaction(transactionId=$transactionId, transactionType=$transactionType, subCategoryId=$subCategoryId, currencyId='$currencyId', transactionNominal=$transactionNominal, transactionDate=$transactionDate, transactionNote=$transactionNote)"
    }
}