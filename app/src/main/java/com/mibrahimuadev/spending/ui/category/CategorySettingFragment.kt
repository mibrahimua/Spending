package com.mibrahimuadev.spending.ui.category

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.CategorySettingListAdapter
import com.mibrahimuadev.spending.data.model.CategoryType
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentCategorySettingBinding

class CategorySettingFragment : Fragment() {

    private var _binding: FragmentCategorySettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategorySettingListAdapter
    private val categoryViewModel: CategoryViewModel by navGraphViewModels(R.id.nav_category) {
        CategoryViewModelFactory(application = requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategorySettingBinding.inflate(layoutInflater)
        val application = requireActivity().application

        val recyclerView = binding.recycleviewCategory
        categoryViewModel.clearLiveDataCategory()
        adapter = CategorySettingListAdapter(application)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(application)

        categoryViewModel.categoryType.observe(viewLifecycleOwner) {
            categoryViewModel.getAllCategoriesByType(it)
        }
        categoryViewModel.allCategories.observe(viewLifecycleOwner, { categories ->
            adapter.setCategory(categories, categoryViewModel.categoryType.value!!)
        })

        binding.radioGroupTransc.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioExpense -> {
                    binding.radioExpense.setTextColor(Color.WHITE)
                    binding.radioIncome.setTextColor(Color.BLACK)
                    categoryViewModel._categoryType.value = CategoryType.EXPENSE
                }
                R.id.radioIncome -> {
                    binding.radioIncome.setTextColor(Color.WHITE)
                    binding.radioExpense.setTextColor(Color.BLACK)
                    categoryViewModel._categoryType.value = CategoryType.INCOME
                }
            }
        }
        binding.radioExpense.isChecked = true

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(requireView())
                        .navigate(CategorySettingFragmentDirections.actionCategorySettingFragmentToHomeFragment())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
                )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}