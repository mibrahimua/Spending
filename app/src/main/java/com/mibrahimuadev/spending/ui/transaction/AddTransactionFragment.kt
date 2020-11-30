package com.mibrahimuadev.spending.ui.transaction

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentAddTransactionBinding
import com.mibrahimuadev.spending.ui.categories.CategoryViewModel
import com.mibrahimuadev.spending.ui.categories.CategoryViewModelFactory
import com.mibrahimuadev.spending.utils.CurrentDate
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class AddTransactionFragment : Fragment(), Calculator {
    private val TAG = "AddTransactionFragment"
    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val addTransactionViewModel: AddTransactionViewModel by navGraphViewModels(R.id.nav_add_transc) {
        AddTransactionViewModelFactory(requireActivity().application)
    }
    private val args: AddTransactionFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "AddTransactionFragment created")
        val application = requireNotNull(this.activity).application
        _binding = FragmentAddTransactionBinding.inflate(layoutInflater)

        /**
         * Set title fragment depend on transaction type (Expense, Income)
         */
        (activity as AppCompatActivity).supportActionBar?.title = args.transactionType.name

        /**
         * Prevent layout to adjust when user use keyboard
         */
        getActivity()?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        val viewModelFactory = CategoryViewModelFactory(application)

        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

        /**
         * Calculator Binding Section
         */
        calc = CalculatorImpl(this, application)
        binding.btnPlus.setOnClickListener { calc.handleOperation(PLUS) }
        binding.btnMinus.setOnClickListener { calc.handleOperation(MINUS) }
        binding.btnMultiply.setOnClickListener { calc.handleOperation(MULTIPLY) }
        binding.btnDivide.setOnClickListener { calc.handleOperation(DIVIDE) }
        binding.btnClear.setOnClickListener { calc.handleClear(); }
        binding.btnClear.setOnLongClickListener { calc.handleReset(); true }
        getNumberButtonIds().forEach {
            it.setOnClickListener { calc.numpadClicked(it.id); }
        }
        binding.btnEquals.setOnClickListener { calc.handleEquals(); }
        binding.result.setOnLongClickListener { copyToClipboard(true) }

        addTransactionViewModel.calcNewFormula.observe(viewLifecycleOwner) { formula ->
            binding.formula.text = formula
        }
        addTransactionViewModel.calcNewResult.observe(viewLifecycleOwner) { result ->
            binding.result.text = result
            calc.setInputDisplayedFormula(result)
        }

        /**
         * Category Binding Section
         */
        binding.btnCategory.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(
                    AddTransactionFragmentDirections.actionAddTransaksiFragmentToAddCategoryTranscFragment(
                        args.transactionType
                    )
                )
        }
        categoryViewModel.getCategory(args.idKategori)
        categoryViewModel.categoryName.observe(viewLifecycleOwner) { result ->
            binding.categoryName.text = result ?: "Category Not Selected"
        }
        addTransactionViewModel._transactionType.value = args.transactionType
        addTransactionViewModel._transactionCategory.value = args.idKategori

        /**
         * Date Transaction Binding Section
         */
        val currentDate = CurrentDate()

        if(addTransactionViewModel._dateTransaction.value == null) {
            saveDatePickerToLiveData(currentDate.day, currentDate.month, currentDate.year)
        }

        binding.btnDate.setOnClickListener {
            showDatePicker(currentDate)
                .show(
                    requireActivity().supportFragmentManager,
                    "Datepickerdialog"
                )
        }
        addTransactionViewModel._dateTransaction.observe(viewLifecycleOwner) { date ->
            val day = currentDate.getCustomDate(date,"dd")
            val month = currentDate.getCustomDate(date,"MM")
            val year = currentDate.getCustomDate(date,"yyyy")
            binding.textDate.text =
                "${day} " + currentDate.monthName[month] + " ${year}"
        }

        /**
         * Note Transaction Binding Section
         */
        binding.noteTransc.setText(addTransactionViewModel.noteTransaction.value)
        binding.noteTransc.addTextChangedListener {
            addTransactionViewModel._noteTransaction.value = binding.noteTransc.text.toString()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.save_action) {

            addTransactionViewModel.saveTransaction()
//            Navigation.findNavController(requireView()).navigate(AddTransactionFragmentDirections.actionAddTransaksiFragmentToHomeFragment())
            Toast.makeText(requireContext(), "save action clicked", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getNumberButtonIds() = arrayOf(
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
        addTransactionViewModel._calcNewResult.value = value
    }

    override fun showNewFormula(value: String, context: Context) {
        addTransactionViewModel._calcNewFormula.value = value
    }

    fun showDatePicker(currentDate: CurrentDate): DatePickerDialog {
        val datePickerDialog =
            DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
                saveDatePickerToLiveData(dayOfMonth, monthOfYear, year)
            }, currentDate.year, currentDate.month, currentDate.day)

        return datePickerDialog
    }

    fun saveDatePickerToLiveData(dayOfMonth: Int, monthOfYear: Int, year: Int) {
        val currentDate = CurrentDate()
        val date = currentDate
            .dateFormat("dd MM yyyy")
            .parse("$dayOfMonth $monthOfYear $year")
        Log.i(TAG, "Saving date into live data $date")
        addTransactionViewModel._dateTransaction.value = date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "AddTransactionFragment destroyed")
    }
}