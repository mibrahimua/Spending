package com.mibrahimuadev.spending.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.TransactionList
import com.mibrahimuadev.spending.ui.home.HomeFragmentDirections
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.Formatter
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
        holder.tglTransaksi.text = CurrentDate.getDateString(current.transactionDate)
        holder.kategoriTransaksi.text = current.categoryName
        holder.nominalTransaksi.text =
            current.currencySymbol + " " + Formatter.addThousandsDelimiter(current.transactionNominal.toString())

        holder.itemView.setOnClickListener { view ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailTransactionFragment2(current.transactionId)
            view.findNavController().navigate(action)
        }
    }
}