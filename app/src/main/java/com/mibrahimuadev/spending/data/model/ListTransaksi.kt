package com.mibrahimuadev.spending.data.model

import java.util.*

data class ListTransaksi(
    override val idTransaksi: Long,
    override val tglTransaksi: Date,
    val lokasiIcon: String,
    val namaKategori: String? = "",
    val namaSubKategori: String? = "",
    override val nominalTransaksi: Double,
    val symbolUang: String? = "",
    override val tipeTransaksi: TipeTransaksi,
    override val catatanTransaksi: String? = ""
) : BaseTransaksi