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
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    override val transactionId: Long = 0L,

    override val transactionType: TransactionType,

    override val transactionIncome: Double?,

    override val transactionExpense: Double?,

    val categoryId: Int,

    override val transactionDate: Date,

    override val transactionNote: String? = ""

) : BaseTransaction {
    override fun toString(): String {
        return "TransactionEntity(transactionId=$transactionId, transactionType=$transactionType, transactionIncome=$transactionIncome, transactionExpense=$transactionExpense, categoryId=$categoryId, transactionDate=$transactionDate, transactionNote=$transactionNote)"
    }
}