package com.mibrahimuadev.spending.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.CustomDatepickerDialogBinding
import com.mibrahimuadev.spending.utils.CurrentDate


class CustomDatePickerFragment : DialogFragment() {
    private var _binding: CustomDatepickerDialogBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by navGraphViewModels(R.id.navigation) {
        HomeViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomDatepickerDialogBinding.inflate(layoutInflater)

//        dialog?.window?.setBackgroundDrawableResource(R.)
        dialog!!.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
        val params = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        params.y = 105
        dialog!!.window!!.attributes = params

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        binding.currentMonth.setOnClickListener {
            homeViewModel._selectedYear.value = CurrentDate.year.toString()
            homeViewModel._selectedMonth.value = CurrentDate.month.plus(1).toString()
            dismiss()
        }
        val monthId = homeViewModel.selectedMonth.value!!.toInt().minus(1)
        binding.yearBefore.setOnClickListener {

            val yearBefore = homeViewModel.selectedYear.value?.toInt()?.minus(1).toString()
            homeViewModel._selectedYear.value = yearBefore

//            getMonthButtonIds()[monthId].let {
//                if(yearBefore != homeViewModel.currentSelectedYear.value) {
//                    it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
//                }
//            }
        }
        binding.yearNext.setOnClickListener {

            val yearAfter = homeViewModel.selectedYear.value?.toInt()?.plus(1).toString()
            homeViewModel._selectedYear.value = yearAfter

//            getMonthButtonIds()[monthId].let {
//                if(yearAfter != homeViewModel.currentSelectedYear.value) {
//                    it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
//                }
//            }
        }
        getMonthButtonIds().forEachIndexed { index, element ->
            element.setOnClickListener {
                homeViewModel._selectedMonth.value = index.plus(1).toString()
                dismiss()
            }
        }

        homeViewModel.selectedYear.observe(viewLifecycleOwner) {
            binding.yearCurrent.text = it
        }
        homeViewModel.selectedMonth.observe(viewLifecycleOwner) {
            /**
             * masi error jika sudah pilih beda tahun terus kembali lagi currentmonth tidak berwarna
             */
            getMonthButtonIds().forEachIndexed { index, element ->
                if (it.toInt().minus(1) == index) {
                    setBackgroundColorMonth(index, R.color.orange)
                } else {
                    setBackgroundColorMonth(index, R.color.white)
                }
            }
        }

//        homeViewModel.currentSelectedYear.observe(viewLifecycleOwner) {
//
//
//        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        val window = dialog?.window
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setDimAmount(0f)
    }

    private fun getMonthButtonIds() = arrayOf(
        binding.monthJan,
        binding.monthFeb,
        binding.monthMar,
        binding.monthApr,
        binding.monthMay,
        binding.monthJun,
        binding.monthJul,
        binding.monthAug,
        binding.monthSep,
        binding.monthOct,
        binding.monthNov,
        binding.monthDec
    )


    private fun setBackgroundColorMonth(monthId: Int, color: Int) {
        getMonthButtonIds()[monthId].let {
            it.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        Log.i("CustomDatePickerDialog", "CustomDatePickerDialog destroyed")
    }
}