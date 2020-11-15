package com.mibrahimuadev.spending.data.model

import java.util.*

interface BaseTransaksi {
    val idTransaksi: Long

    val tipeTransaksi: TipeTransaksi

    val nominalTransaksi: Double

    val tglTransaksi: Date

    val catatanTransaksi: String?

}