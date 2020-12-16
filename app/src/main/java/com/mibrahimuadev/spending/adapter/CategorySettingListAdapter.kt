package com.mibrahimuadev.spending.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.Category
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.ui.categories.CategorySettingFragmentDirections

class CategorySettingListAdapter internal constructor(private val context: Context) :
    RecyclerView.Adapter<CategorySettingListAdapter.CategoryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val categories = mutableListOf<Category>()
    private lateinit var categoryType: TransactionType

    inner class CategoryViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {
        var categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryName: TextView = itemVIew.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = categories[position]
        val resId = this.context.resources.getIdentifier(current.iconName,"drawable", this.context.packageName)
        holder.categoryIcon.setImageResource(resId)
        holder.categoryName.text = current.categoryName
        holder.itemView.setOnClickListener { view ->
            val action = CategorySettingFragmentDirections.actionCategorySettingFragmentToAddEditCategoryFragment()
                .setCategoryId(current.categoryId)
                .setActionType("UPDATE")
            view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    internal fun setCategory(
        category: List<Category>,
        typeCategory: TransactionType
    ) {
        this.categories.removeAll(categories)
        this.categories.addAll(category)
        this.categoryType = typeCategory
        notifyDataSetChanged()
    }
}