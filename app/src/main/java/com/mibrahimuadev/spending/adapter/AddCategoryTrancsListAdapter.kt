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
import com.mibrahimuadev.spending.ui.transaction.AddCategoryTranscFragmentDirections

class AddCategoryTrancsListAdapter internal constructor(
    private val context: Context,
    private val listener: (Category) -> Unit
) :
    RecyclerView.Adapter<AddCategoryTrancsListAdapter.CategoryViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var categories = mutableListOf<Category>()
    private var categoriesCopy = mutableListOf<Category>()
    private var lastCategoryId: Int = 0
    private var isNewCategory: Boolean = false
    private lateinit var categoryType: TransactionType

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    internal fun setLastCategoryId(lastId: Int) {
        this.lastCategoryId = lastId
        notifyDataSetChanged()
    }

    internal fun setCategory(
        category: List<Category>,
        typeCategory: TransactionType,
    ) {
        this.categories.addAll(category)
        categoriesCopy.addAll(category)
        this.categoryType = typeCategory
        notifyDataSetChanged()
    }

    internal fun setFilter(filterText: String?) {
        categories.clear()
        if (filterText?.isEmpty()!!) {
            categories.addAll(categoriesCopy)
            isNewCategory = false
        } else {
            for (category in categoriesCopy) {
                if (category.categoryName?.toLowerCase()?.contains(filterText.toLowerCase())!!) {
                    isNewCategory = false
                    categories.add(category)
                }
            }
            /**
             * if the category does not exist, then display the add category option
             */
            if (categories.size == 0) {
                isNewCategory = true
                categories.add(
                    Category(
                        categoryId = lastCategoryId,
                        categoryName = filterText,
                        categoryType = categoryType,
                        iconId = 53,
                        iconName = "ic_money_banknote_12"
                    )
                )
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        val resId = this.context.resources.getIdentifier(
            item.iconName,
            "drawable",
            this.context.packageName
        )
        holder.categoryIcon.setImageResource(resId)
        holder.categoryName.text = let {
            if (isNewCategory) {
                "Add Category : " + item.categoryName
            } else {
                item.categoryName
            }
        }

        holder.itemView.setOnClickListener { view ->
            listener(item)
            val action =
                AddCategoryTranscFragmentDirections.actionAddCategoryTranscFragmentToAddTransactionFragment2()
                    .setTransactionType(categoryType.name)
            view.findNavController().navigate(action)

        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}