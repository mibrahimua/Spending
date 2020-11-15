package com.mibrahimuadev.spending.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.ListTransaksi

class TransaksiListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<TransaksiListAdapter.TransaksiViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var transaksi = emptyList<ListTransaksi>()

    inner class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tglTransaksi: TextView = itemView.findViewById(R.id.tgl_transaksi)
        val kategoriTransaksi: TextView = itemView.findViewById(R.id.kategori_transaksi)
        val nominalTransaksi: TextView = itemView.findViewById(R.id.nominal_transaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_transaksi, parent, false)
        return TransaksiViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transaksi.size
    }

    internal fun setTransaksi(transaksi: List<ListTransaksi>) {
        this.transaksi = transaksi
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        val current = transaksi[position]
        holder.tglTransaksi.text = current.lokasiIcon
        holder.kategoriTransaksi.text = current.namaSubKategori
        holder.nominalTransaksi.text =
            current.symbolUang + " " + current.nominalTransaksi.toString()
    }
}