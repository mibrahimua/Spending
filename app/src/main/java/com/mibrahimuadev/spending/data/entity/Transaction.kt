package com.mibrahimuadev.spending.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.data.model.BaseTransaksi
import com.mibrahimuadev.spending.data.model.TipeTransaksi
import java.util.*

@Entity(
    tableName = "transaksi",
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
    override val idTransaksi: Long,

    override val tipeTransaksi: TipeTransaksi,

    val idSubKategori: Int,

    val idMataUang: String,

    override val nominalTransaksi: Double,

    override val tglTransaksi: Date,

    override val catatanTransaksi: String? = ""

) : BaseTransaksi {
    override fun toString(): String {
        return "Transaksi(idTransaksi=$idTransaksi, tipeTransaksi=$tipeTransaksi, idSubKategori=$idSubKategori, idMataUang='$idMataUang', nominalTransaksi=$nominalTransaksi, tglTransaksi=$tglTransaksi, catatanTransaksi=$catatanTransaksi)"
    }
}