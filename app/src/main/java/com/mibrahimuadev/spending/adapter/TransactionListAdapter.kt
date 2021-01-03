package com.mibrahimuadev.spending.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.Transaction
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.ui.home.HomeFragmentDirections
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.Formatter

class TransactionListAdapter internal constructor(private val context: Context) :
    RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var transaction = emptyList<Transaction>()

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tglTransaksi: TextView = itemView.findViewById(R.id.tgl_transaksi)
        val kategoriTransaksi: TextView = itemView.findViewById(R.id.kategori_transaksi)
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val nominalTransaksi: TextView = itemView.findViewById(R.id.nominal_transaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transaction.size
    }

    internal fun setTransaksi(transaction: List<Transaction>) {
        this.transaction = transaction
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = transaction[position]
        val resId = this.context.resources.getIdentifier(
            current.iconName,
            "drawable",
            this.context.packageName
        )

        holder.categoryIcon.setImageResource(resId)
        holder.tglTransaksi.text = CurrentDate.getDateString(current.transactionDate)
        holder.kategoriTransaksi.text = current.categoryName
        holder.nominalTransaksi.text =
            current.currencySymbol + " " + Formatter.addThousandsDelimiter(
                convertToTransactionNominal(current)
            )

        holder.itemView.setOnClickListener { view ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailTransactionFragment2(current.transactionId)
            view.findNavController().navigate(action)
        }
    }

    private fun convertToTransactionNominal(transaction: Transaction): String {
        var transactionNominal = ""
        if (transaction.transactionType.equals(TransactionType.INCOME)) {
            transactionNominal = transaction.transactionIncome.toString()
        } else if (transaction.transactionType.equals(TransactionType.EXPENSE)) {
            transactionNominal = transaction.transactionExpense.toString()
        }
        return transactionNominal
    }
}