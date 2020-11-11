package com.mibrahimuadev.spending.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_kategori",
//    foreignKeys = arrayOf(
////        ForeignKey(
////            entity = IconTransaksi::class,
////            parentColumns = arrayOf("id_icon"),
////            childColumns = arrayOf("id_icon"),
////            onDelete = ForeignKey.CASCADE
////        ),
//        ForeignKey(
//            entity = Kategori::class,
//            parentColumns = arrayOf("id_kategori"),
//            childColumns = arrayOf("id_kategori"),
//            onDelete = ForeignKey.CASCADE
//        )
//    )
)
data class SubKategori(
    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id_sub_kategori")
    val idSubKategori: Int,

//    @ColumnInfo(name = "id_kategori", index = true)
    val idKategori: Int,

//    @ColumnInfo(name = "nama_sub_kategori")
    val namaSubKategori: String?,

//    @ColumnInfo(name = "id_icon", index = true)
    val idIcon: Int?
)