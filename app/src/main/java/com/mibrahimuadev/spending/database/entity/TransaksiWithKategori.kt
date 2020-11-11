package com.mibrahimuadev.spending.database.entity

import com.mibrahimuadev.spending.database.model.*

data class TransaksiWithKategori(
    override val idTransaksi: Int,
    override val tipeTransaksi: TipeTransaksi,
    override val nominal: Double,
    override val idKategori: Int,
    override val namaKategori: String,
    override val idIcon: Int,
    override val namaIcon: String?,
    override val lokasiIcon: String?,
    override val idMataUang: Int,
    override val namaUang: String?,
    override val namaUangShort: String?
) : TransaksiImpl, KategoriImpl, IconTransaksiImpl, MataUangImpl