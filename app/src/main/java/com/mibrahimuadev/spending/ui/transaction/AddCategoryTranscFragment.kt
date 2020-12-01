package com.mibrahimuadev.spending.ui.transaction

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.CategoryListAdapter
import com.mibrahimuadev.spending.databinding.FragmentAddCategoryTranscBinding
import com.mibrahimuadev.spending.ui.categories.CategoryViewModel
import com.mibrahimuadev.spending.ui.categories.CategoryViewModelFactory


class AddCategoryTranscFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentAddCategoryTranscBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoryListAdapter
    private val TransactionViewModel: TransactionViewModel by navGraphViewModels(R.id.nav_transc) {
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
        adapter = CategoryListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)

        val viewModelFactory = CategoryViewModelFactory(application)

        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)
        categoryViewModel.getAllCategories(args.typeCategory)
        categoryViewModel.allCategories.observe(viewLifecycleOwner, { categories ->
            adapter.setCategory(categories, args.typeCategory)
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_category, menu)
        // Get the SearchView and set the searchable configuration
        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.onActionViewExpanded()
        searchView.clearFocus()
        searchView.queryHint = "Search or add category"
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_search) {
            Toast.makeText(activity, "Grid item touched", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.setFilter(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.setFilter(newText)
        return true
    }
}