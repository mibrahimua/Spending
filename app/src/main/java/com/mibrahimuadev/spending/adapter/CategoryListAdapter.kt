package com.mibrahimuadev.spending.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.CategoryList

class CategoryListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var category = emptyList<CategoryList>()

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_category_transc, parent, false)
        return CategoryViewHolder(itemView)
    }

    internal fun setCategory(category: List<CategoryList>) {
        this.category = category
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = category[position]
//        holder.categoryIcon = R.drawable.ic_launcher_background
        holder.categoryName.text = current.namaKategori
    }

    override fun getItemCount(): Int {
        return category.size
    }
}