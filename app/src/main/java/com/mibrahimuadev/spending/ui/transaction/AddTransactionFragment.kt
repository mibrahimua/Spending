package com.mibrahimuadev.spending.ui.transaction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentAddTransactionBinding
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class AddTransactionFragment : Fragment(), Calculator {

    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val addTransactionViewModel: AddTransactionViewModel by navGraphViewModels(R.id.nav_add_transc) {
        AddTransactionViewModelFactory(requireActivity().application)
    }
    private val args: AddTransactionFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val application = requireNotNull(this.activity).application
        Log.i("AddTransactionFragment", "AddTransactionFragment created")
        _binding = FragmentAddTransactionBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application

        (activity as AppCompatActivity).supportActionBar?.title = args.transactionType.name

        getActivity()?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        calc = CalculatorImpl(this, application)

        binding.btnPlus.setOnClickListener { calc.handleOperation(PLUS) }
        binding.btnMinus.setOnClickListener { calc.handleOperation(MINUS) }
        binding.btnMultiply.setOnClickListener { calc.handleOperation(MULTIPLY) }
        binding.btnDivide.setOnClickListener { calc.handleOperation(DIVIDE) }

        binding.btnClear.setOnClickListener { calc.handleClear(); }
        binding.btnClear.setOnLongClickListener { calc.handleReset(); true }
        getButtonIds().forEach {
            it.setOnClickListener { calc.numpadClicked(it.id); }
        }

        binding.btnEquals.setOnClickListener { calc.handleEquals(); }
        binding.result.setOnLongClickListener { copyToClipboard(true) }

        binding.btnCategory.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(AddTransactionFragmentDirections.actionAddTransaksiFragmentToAddCategoryTranscFragment(args.transactionType))
        }

        addTransactionViewModel.calcNewFormula.observe(viewLifecycleOwner) { formula ->
            binding.formula.text = formula
        }

        addTransactionViewModel.calcNewResult.observe(viewLifecycleOwner) { result ->
            binding.result.text = result
            calc.setInputDisplayedFormula(result)
        }

        addTransactionViewModel.getCategory(args.idKategori)
        addTransactionViewModel.categoryName.observe(viewLifecycleOwner) { result ->
            binding.categoryName.text = result
        }


        val now = Calendar.getInstance()
        val currentYear: Int = now.get(Calendar.YEAR)
        val currentMonth: Int = now.get(Calendar.MONTH)
        val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
                val month = listOf(
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "Jun",
                    "Jul",
                    "Aug",
                    "Sep",
                    "Oct",
                    "Nov",
                    "Dec"
                ).let {
                    it[monthOfYear]
                }
                binding.textDate.text = "$dayOfMonth $month $year"

            }, currentYear, currentMonth, currentDay)
        binding.btnDate.setOnClickListener {
//            datePickerDialog.setTitle("INI JUDUL")
//            datePickerDialog.setOkText("SIP")
//            datePickerDialog.setCancelText("GA JADI")
            datePickerDialog.show(requireActivity().supportFragmentManager, "Datepickerdialog")
        }


        return binding.root
    }

    private fun getButtonIds() = arrayOf(
        binding.btnDecimal,
        binding.btn0,
        binding.btn1,
        binding.btn2,
        binding.btn3,
        binding.btn4,
        binding.btn5,
        binding.btn6,
        binding.btn7,
        binding.btn8,
        binding.btn9
    )

    private fun copyToClipboard(copyResult: Boolean): Boolean {
        var value = binding.formula.text.toString().trim()
        if (copyResult) {
            value = binding.result.text.toString().trim()
        }

        return if (value.isEmpty()) {
            false
        } else {
            val clipBoard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("text", value)
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(activity, "Copied", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun showNewResult(value: String, context: Context) {
//        binding.result.text = value
        addTransactionViewModel._calcNewResult.value = value
    }

    override fun showNewFormula(value: String, context: Context) {
//        binding.formula.text = value
        addTransactionViewModel._calcNewFormula.value = value

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("AddTransactionFragment", "AddTransactionFragment destroyed")
    }
}