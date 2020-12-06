package com.mibrahimuadev.spending.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.CategoryList
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.ui.transaction.AddCategoryTranscFragmentDirections

class AddCategoryTrancsListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<AddCategoryTrancsListAdapter.CategoryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var categories = mutableListOf<CategoryList>()
    private var categoriesCopy = mutableListOf<CategoryList>()
    private var lastCategoryId: Int = 0
    private var isNewCategory: Boolean = false
    private lateinit var typeCategory: TransactionType

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = inflater.inflate(R.layout.recycleview_category_transc, parent, false)
        return CategoryViewHolder(itemView)
    }

    internal fun setLastCategoryId(lastId: Int) {
        this.lastCategoryId = lastId
        notifyDataSetChanged()
    }

    internal fun setCategory(
        category: List<CategoryList>,
        typeCategory: TransactionType
    ) {
        this.categories.addAll(category)
        categoriesCopy.addAll(category)
        this.typeCategory = typeCategory
        notifyDataSetChanged()
    }

    internal fun setFilter(filterText: String?) {
        categories.clear()
        if (filterText?.isEmpty()!!) {
            categories.addAll(categoriesCopy)
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
                    CategoryList(
                        categoryId = lastCategoryId,
                        categoryName = filterText
                    )
                )
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = categories[position]
//        holder.categoryIcon = R.drawable.ic_launcher_background
        holder.categoryName.text = let {
            if (isNewCategory) {
                "Add Category : " + current.categoryName
            } else {
                current.categoryName
            }
        }

        holder.itemView.setOnClickListener { view ->
            val action =
                AddCategoryTranscFragmentDirections.actionAddCategoryTranscFragmentToAddTransactionFragment2()
                    .setCategoryId(current.categoryId)
                    .setCategoryName(current.categoryName)
                    .setTransactionType(typeCategory.name)
            view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}