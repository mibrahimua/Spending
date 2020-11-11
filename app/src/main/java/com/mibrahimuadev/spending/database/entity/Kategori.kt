package com.mibrahimuadev.spending.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mibrahimuadev.spending.database.model.KategoriImpl


@Entity(
    tableName = "kategori",
//    foreignKeys = arrayOf(
//        ForeignKey(
//            entity = IconTransaksi::class,
//            parentColumns = arrayOf("id_icon"),
//            childColumns = arrayOf("id_icon"),
//            onDelete = ForeignKey.CASCADE
//        )
//    )
)
data class Kategori(
    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id_kategori")
    override val idKategori: Int,

//    @ColumnInfo(name = "nama_kategori")
    override val namaKategori: String? = "",

//    @ColumnInfo(name = "id_icon", index = true)
    val idIcon: Int

) : KategoriImpl