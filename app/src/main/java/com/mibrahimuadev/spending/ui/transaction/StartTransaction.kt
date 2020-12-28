package com.mibrahimuadev.spending.ui.transaction

interface StartTransaction : SetupTransaction, DisplayTransaction {
    fun startTransaction()

    fun setupTypeTransaction()

    fun setupCalculator()

    fun setupCategory()

    fun setupDateTransaction()

    fun setupNoteTransaction()
}