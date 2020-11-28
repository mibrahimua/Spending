package com.mibrahimuadev.spending.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.utils.Formatter
import java.text.SimpleDateFormat
import java.util.*

class TransactionListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var transaksi = emptyList<TransactionList>()

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tglTransaksi: TextView = itemView.findViewById(R.id.tgl_transaksi)
        val kategoriTransaksi: TextView = itemView.findViewById(R.id.kategori_transaksi)
        val nominalTransaksi: TextView = itemView.findViewById(R.id.nominal_transaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_transaksi, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transaksi.size
    }

    internal fun setTransaksi(transaction: List<TransactionList>) {
        this.transaksi = transaction
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = transaksi[position]
        holder.tglTransaksi.text = getDateString(current.transactionDate)
        holder.kategoriTransaksi.text = current.categoryName
        holder.nominalTransaksi.text =
            current.currencySymbol + " " + addThousandsDelimiter(current.transactionNominal.toString())
    }

    private fun getDateString(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    private fun addThousandsDelimiter(nominalTransaction: String): String {
        var convertNominal = ""
        val numbersRegex = "[^0-9,.]".toRegex()
        val valueToCheck =
            numbersRegex.split(nominalTransaction).filter { it.trim().isNotEmpty() }
        valueToCheck.forEach {
            var newString = Formatter.addGroupingSeparators(it)
            // allow writing numbers like 0.0003 but not allow if number only like 0.0
            if (it.contains(".") && it.substringAfter(".") != "0") {
                newString = newString.substringBefore(".") + ".${it.substringAfter(".")}"
            }
            convertNominal = nominalTransaction.replace(it, newString)
        }
        return convertNominal
    }
}