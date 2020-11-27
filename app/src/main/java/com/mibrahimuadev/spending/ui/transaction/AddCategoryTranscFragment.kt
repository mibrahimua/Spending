package com.mibrahimuadev.spending.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.CategoryListAdapter
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.databinding.FragmentAddCategoryTranscBinding
import com.mibrahimuadev.spending.ui.categories.CategoryViewModel
import com.mibrahimuadev.spending.ui.categories.CategoryViewModelFactory

class AddCategoryTranscFragment : Fragment() {

    private var _binding: FragmentAddCategoryTranscBinding? = null
    private val binding get() = _binding!!
    private val addTransactionViewModel: AddTransactionViewModel by navGraphViewModels(R.id.nav_add_transc) {
        defaultViewModelProviderFactory
    }
    private val args: AddCategoryTranscFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddCategoryTranscBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        val recycleView = binding.recycleviewCategoryTransc
        val adapter = CategoryListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)

        val viewModelFactory = CategoryViewModelFactory(application)

        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)
        categoryViewModel.getAllCategories(args.typeCategory)
        categoryViewModel.allCategories.observe(viewLifecycleOwner, { categories ->
                adapter.setCategory(categories)
        })

        return binding.root
    }


}