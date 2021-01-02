package com.mibrahimuadev.spending.ui.categories

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentAddeditCategoryBinding
import com.mibrahimuadev.spending.utils.EventObserver


class AddEditCategoryFragment : Fragment() {

    private var _binding: FragmentAddeditCategoryBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditCategoryFragmentArgs by navArgs()
    private val categoryViewModel: CategoryViewModel by navGraphViewModels(R.id.nav_category) {
        CategoryViewModelFactory(application = requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddeditCategoryBinding.inflate(layoutInflater)
        val application = requireActivity().application
        categoryViewModel.getDetailCategory(args.categoryId)
        setTextOnCategoryIcon()
        categoryViewModel.dataLoading.observe(viewLifecycleOwner) { loading ->
            if (loading == false) {
                binding.etCategoryName.setText(categoryViewModel.categoryName.value)
            }
        }
        binding.etCategoryName.addTextChangedListener {
            categoryViewModel.editTextCategoryChanged(it.toString())
        }

        categoryViewModel.iconName.observe(viewLifecycleOwner) {
            setTextOnCategoryIcon()
            val resId = application.resources.getIdentifier(
                it,
                "drawable",
                application.packageName
            )
            binding.categoryIcon.setImageResource(resId)
        }
        binding.categoryIconSelect.setOnClickListener {
            /**
             * Close keyboard before navigate
             */
            val imm =
                getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
            Navigation.findNavController(requireView())
                .navigate(AddEditCategoryFragmentDirections.actionAddEditCategoryFragmentToIconCategoryFragment())
        }

//        val spCategoryType = binding.spCategoryType
//        spCategoryType.adapter = ArrayAdapter<TransactionType>(
//            requireActivity(),
//            R.layout.spinner_category_type,
//            R.id.textCategoryType,
//            TransactionType.values()
//        )
//        categoryViewModel.categoryType.observe(viewLifecycleOwner) {
//            spCategoryType.setSelection(it.ordinal)
//        }
//        spCategoryType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
//            AdapterView.OnItemClickListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                categoryViewModel._categoryType.value =
//                    TransactionType.valueOf(p0?.selectedItem.toString())
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//
//            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//            }
//        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(requireView())
                        .navigate(AddEditCategoryFragmentDirections.actionGlobalCategorySettingFragment())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_action) {
            categoryViewModel.validateCategory()

            categoryViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
            categoryViewModel.navigateToHome.observe(viewLifecycleOwner, EventObserver {
                val action =
                    AddEditCategoryFragmentDirections.actionGlobalCategorySettingFragment()
                findNavController().navigate(action)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setTextOnCategoryIcon() {
        if(categoryViewModel.iconName.value.isNullOrEmpty()) {
            binding.categoryIconSelect.text = "No Icon Selected"
        } else {
            binding.categoryIconSelect.text = ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}