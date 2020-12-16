package com.mibrahimuadev.spending.ui.categories

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentAddeditCategoryBinding

class AddEditCategoryFragment : Fragment() {

    private var _binding: FragmentAddeditCategoryBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditCategoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddeditCategoryBinding.inflate(layoutInflater)
        val application = requireActivity().application
        val viewModelFactory = CategoryViewModelFactory(application)
        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)


        args.actionType
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_action) {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}