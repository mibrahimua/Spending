package com.mibrahimuadev.spending.ui.icon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentIconCategoryBinding
import com.mibrahimuadev.spending.ui.categories.CategoryViewModel
import com.mibrahimuadev.spending.ui.categories.CategoryViewModelFactory

class IconCategoryFragment : Fragment() {

    private var _binding: FragmentIconCategoryBinding? = null
    private val binding get() = _binding!!
    private val categoryViewModel: CategoryViewModel by navGraphViewModels(R.id.nav_category) {
        CategoryViewModelFactory(application = requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIconCategoryBinding.inflate(layoutInflater)
        val application = requireActivity().application
        val viewModelFactory = IconCategoryViewModelFactory(application = application)
        val iconCategoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(IconCategoryViewModel::class.java)

        getIconIds().forEach {
            it.setOnClickListener {
                val resourceName = this.context?.resources?.getResourceEntryName(it.id)
                Toast.makeText(requireContext(), "Icon ${resourceName}", Toast.LENGTH_SHORT).show()
                categoryViewModel.getIconCategoryByName(resourceName!!)
                Navigation.findNavController(requireView())
                    .navigate(IconCategoryFragmentDirections.actionIconCategoryFragmentToAddEditCategoryFragment())
            }
        }

        return binding.root
    }

    private fun getIconIds() = setOf(
        binding.icEntertainmentsPaintbrush7,
        binding.icEntertainmentsVideo12,
        binding.icEntertainmentsVideo3,
        binding.icEntertainmentsVideo9,
        binding.icEntertainmentsVideoCamera12,
        binding.icEntertainmentsVideoCameraThin,
        binding.icFoodCandy12,
        binding.icFoodCandy14,
        binding.icFoodCandy18,
        binding.icFoodCandy2,
        binding.icFoodCandy22,
        binding.icFoodCandy24,
        binding.icFoodCandy30,
        binding.icFoodCandy8,
        binding.icFoodEat6Thin,
        binding.icFoodFastFood10,
        binding.icFoodFastFood14,
        binding.icFoodFastFood16,
        binding.icFoodFastFood18,
        binding.icFoodFastFood2,
        binding.icFoodFastFood20,
        binding.icFoodFastFood6,
        binding.icFoodFastFood8,
        binding.icGamesGamepad4,
        binding.icGamesPuzzle2,
        binding.icGamesSteam5,
        binding.icHealthMedical11,
        binding.icHealthMedical11,
        binding.icHealthMedical15,
        binding.icHealthMedical16,
        binding.icHealthMedical17,
        binding.icHealthMedical4,
        binding.icHealthMedical5,
        binding.icHealthMedical7,
        binding.icMoneyBanknote12,
        binding.icMoneyBanknote15,
        binding.icMoneyChristmas42,
        binding.icMoneyCreditCard16,
        binding.icShoppingGlasses7,
        binding.icShoppingShoppingBag2,
        binding.icShoppingShoppingBag5,
        binding.icShoppingShoppingBag6,
        binding.icShoppingShoppingCart2,
        binding.icShoppingShoppingCart24,
        binding.icSportsBaseball2,
        binding.icSportsBasketball2,
        binding.icSportsBuilding40,
        binding.icSportsGolf2,
        binding.icSportsSoccer1,
        binding.icTransportationAirport15,
        binding.icTransportationAirport17,
        binding.icTransportationAirport19,
        binding.icTransportationBasket4,
        binding.icTransportationBicycle1,
        binding.icTransportationBus8,
        binding.icTransportationCar2,
        binding.icTransportationCar20,
        binding.icTransportationCar23,
        binding.icTransportationCar6
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}