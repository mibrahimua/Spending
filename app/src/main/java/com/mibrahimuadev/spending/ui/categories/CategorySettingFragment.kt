package com.mibrahimuadev.spending.ui.categories

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.CategorySettingListAdapter
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentCategorySettingBinding

class CategorySettingFragment : Fragment() {

    private var _binding: FragmentCategorySettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategorySettingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategorySettingBinding.inflate(layoutInflater)
        val application = requireActivity().application
        val viewModelFactory = CategoryViewModelFactory(application)

        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)
        val recyclerView = binding.recycleviewCategory

        adapter = CategorySettingListAdapter(application)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(application)

        categoryViewModel.typeCategory.observe(viewLifecycleOwner) {
            categoryViewModel.getAllCategoriesByType(it)
        }
        categoryViewModel.allCategories.observe(viewLifecycleOwner, { categories ->
            adapter.setCategory(categories, categoryViewModel.typeCategory.value!!)
        })

        binding.radioGroupTransc.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioExpense -> {
                    binding.radioExpense.setTextColor(Color.WHITE)
                    binding.radioIncome.setTextColor(Color.BLUE)
                    categoryViewModel._typeCategory.value = TransactionType.EXPENSE
                }
                R.id.radioIncome -> {
                    binding.radioIncome.setTextColor(Color.WHITE)
                    binding.radioExpense.setTextColor(Color.BLUE)
                    categoryViewModel._typeCategory.value = TransactionType.INCOME
                }
            }
        }
        binding.radioExpense.isChecked = true

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_action) {
            Navigation.findNavController(requireView())
                .navigate(
                    CategorySettingFragmentDirections.actionCategorySettingFragmentToAddEditCategoryFragment()
                        .setActionType("INSERT")
                )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}