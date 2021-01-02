package com.mibrahimuadev.spending.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.AddCategoryTrancsListAdapter
import com.mibrahimuadev.spending.databinding.FragmentAddCategoryTranscBinding
import com.mibrahimuadev.spending.ui.categories.CategoryViewModel
import com.mibrahimuadev.spending.ui.categories.CategoryViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class AddCategoryTranscFragment : Fragment(), SearchView.OnQueryTextListener {
    private val TAG = "AddCategoryTranscFragment"
    private var _binding: FragmentAddCategoryTranscBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AddCategoryTrancsListAdapter
    private lateinit var searchView: SearchView
    private val args: AddCategoryTranscFragmentArgs by navArgs()
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.nav_transc) {
        TransactionViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "AddCategoryTranscFragment created")
        _binding = FragmentAddCategoryTranscBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        val recyclerView = binding.recycleviewCategoryTransc

        val viewModelFactory = CategoryViewModelFactory(application)

        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

        adapter = AddCategoryTrancsListAdapter(application) {
//            Toast.makeText(application, "category $it clicked", Toast.LENGTH_SHORT).show()
            runBlocking {
                Log.i(TAG, "Coroutine cek if category exist starting")
                categoryViewModel._categoryId.value = it.categoryId
                categoryViewModel._categoryName.value = it.categoryName
                categoryViewModel._iconId.value = it.iconId
                categoryViewModel._categoryType.value = it.categoryType
                val job2 = lifecycleScope.launch(Dispatchers.IO) {
                    categoryViewModel.insertOrUpdateCategory()
                }
                job2.join()

                Log.i(TAG, "Coroutine ends " + job2.isActive)
            }
            transactionViewModel._categoryId.value = it.categoryId
            transactionViewModel._categoryName.value = it.categoryName
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(application)

        categoryViewModel.getAllCategoriesByType(args.categoryType)
        categoryViewModel.allCategories.observe(viewLifecycleOwner, { categories ->
            adapter.setCategory(categories, args.categoryType)
        })
        categoryViewModel.getLastCategoryId()
        categoryViewModel.lastCategoryId.observe(viewLifecycleOwner) {
            adapter.setLastCategoryId(it)
        }

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        // Get the SearchView and set the searchable configuration
        val searchItem = menu.findItem(R.id.menu_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.onActionViewExpanded()
        searchView.clearFocus()
        searchView.queryHint = "Search or add category"
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.setFilter(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.setFilter(newText)
        return true
    }

    override fun onDetach() {
        super.onDetach()
        searchView.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.i(TAG, "AddCategoryTranscFragment destroyed")
    }
}