package com.mibrahimuadev.spending.database.entity

import androidx.room.*
import com.mibrahimuadev.spending.database.model.TipeTransaksi
import com.mibrahimuadev.spending.database.model.TransaksiImpl

@Entity(
    tableName = "transaksi",
//    foreignKeys = arrayOf(
//        ForeignKey(
//            entity = MataUang::class,
//            parentColumns = arrayOf("id_mata_uang"),
//            childColumns = arrayOf("id_mata_uang"),
//            onDelete = ForeignKey.CASCADE
//        )
//    )
)
data class Transaksi (
    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id_transaksi")
    override val idTransaksi: Int,

//    @ColumnInfo(name = "tipe_transaksi")
    override val tipeTransaksi: TipeTransaksi,

//    @ColumnInfo(name = "id_kategori")
    val idKategori: Int,

//    @ColumnInfo(name = "id_mata_uang", index = true)
    val idMataUang: Int,

//    @ColumnInfo(name = "nominal")
    override val nominal: Double,

    ) : TransaksiImpl{
    override fun toString(): String {
        return "Transaksi(idTransaksi=$idTransaksi, tipeTransaksi='$tipeTransaksi', idKategori=$idKategori, idMataUang=$idMataUang, nominal=$nominal)"
    }
}