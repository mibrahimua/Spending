package com.mibrahimuadev.spending.ui.transaction

interface PrepareTransaction : DisplayTransaction {
    fun onCreateNewTransaction()

    fun onEditTransaction()

    fun onDataNotAvailable()

}