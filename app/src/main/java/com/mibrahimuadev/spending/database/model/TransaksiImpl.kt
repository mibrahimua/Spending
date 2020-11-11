package com.mibrahimuadev.spending.database.model

interface TransaksiImpl {
    val idTransaksi: Int

    val tipeTransaksi: TipeTransaksi

    val nominal: Double

}