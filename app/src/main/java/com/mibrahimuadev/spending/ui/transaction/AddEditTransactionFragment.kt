package com.mibrahimuadev.spending.ui.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentAddTransactionBinding
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.wrapper.EventObserver
import com.mibrahimuadev.spending.utils.calculator.Calculator
import com.mibrahimuadev.spending.utils.calculator.CalculatorImpl
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog


class AddEditTransactionFragment : Fragment(), Calculator {

    private val TAG = "AddTransactionFragment"
    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.nav_transaction) {
        TransactionViewModelFactory(requireActivity().application)
    }
    private val args: AddEditTransactionFragmentArgs by navArgs()
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    val Float.toPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "AddTransactionFragment created")

        _binding = FragmentAddTransactionBinding.inflate(layoutInflater)

        /**
         * Prevent layout to adjust when user use keyboard
         */
        getActivity()?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        startTransaction()

        binding.noteTransc.setOnClickListener {
            val editText: EditText = EditText(requireContext())
            editText.text.append(transactionViewModel.noteTransaction.value ?: "")
            AlertDialog.Builder(context)
                .setTitle("Note Transaction")
                .setEditText(editText)
                .setNegativeButton("Cancel") { _, _ ->
                }
                .setPositiveButton("Set") { _, _ ->
                    transactionViewModel._noteTransaction.value = editText.text.toString()
                }
                .show()
        }
        transactionViewModel.noteTransaction.observe(viewLifecycleOwner) {
            binding.noteTransc.text = it
        }
        setHasOptionsMenu(true)
        return binding.root
    }



    fun startTransaction() {
        setupDataTransaction()
        displayDataTransaction()
    }

    fun setupDataTransaction() {
        /**
         * Init Transaction
         */
        transactionViewModel.transactionTypeArgs = TransactionType.valueOf(args.transactionType!!)
        transactionViewModel.transactionIdArgs = args.transactionId

        setupTypeTransaction()
        setupCalculator()
        setupCategory()
        setupDateTransaction()
        setupNoteTransaction()
    }

    fun displayDataTransaction() {
        transactionViewModel.displayDataTransaction()
    }

    fun setupTypeTransaction() {
        /**
         *  Transaction Type View Binding Section
         */
        transactionViewModel.transactionType.observe(viewLifecycleOwner) {

            if (it == TransactionType.EXPENSE) {
                binding.radioExpense.isChecked = true
            } else if (it == TransactionType.INCOME) {
                binding.radioIncome.isChecked = true
            }
        }
        binding.radioGroupTransc.setOnCheckedChangeListener { group, checkedId ->
            var transactionType: TransactionType? = null
            when (checkedId) {
                R.id.radioExpense -> {
                    binding.radioExpense.setTextColor(Color.WHITE)
                    binding.radioIncome.setTextColor(Color.BLACK)
                    transactionType = TransactionType.EXPENSE
                }
                R.id.radioIncome -> {
                    binding.radioIncome.setTextColor(Color.WHITE)
                    binding.radioExpense.setTextColor(Color.BLACK)
                    transactionType = TransactionType.INCOME
                }
            }
            if (transactionViewModel.transactionType.value != transactionType) {
                transactionViewModel.resetCategory()
            }
            transactionViewModel._transactionType.value = transactionType
        }
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

    fun setupCalculator() {
        /**
         * Calculator View Binding Section
         */
        val application = requireNotNull(this.activity).application
        calc = CalculatorImpl(this, application)
        binding.btnPlus.setOnClickListener {
            calc.handleOperation(PLUS)
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
        }
        binding.btnMinus.setOnClickListener {
            calc.handleOperation(MINUS)
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
        }
        binding.btnMultiply.setOnClickListener {
            calc.handleOperation(MULTIPLY)
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
        }
        binding.btnDivide.setOnClickListener {
            calc.handleOperation(DIVIDE)
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
        }
        binding.btnClear.setOnClickListener {
            calc.handleClear()
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
        }
        binding.btnClear.setOnLongClickListener {
            calc.handleReset()
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
            true
        }
        getNumberButtonIds().forEach {
            it.setOnClickListener {
                calc.numpadClicked(it.id);
                transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
            }
        }
        binding.btnEquals.setOnClickListener {
            calc.handleEquals()
            transactionViewModel.saveCalculatorState(calc.lastOperation, calc.lastKey)
        }
        binding.result.setOnLongClickListener { copyToClipboard(true) }
        if (transactionViewModel.transactionNominal.value == null) {
            transactionViewModel._transactionNominal.value = "0"
        }
        transactionViewModel.calcLastKey.observe(viewLifecycleOwner) {
            calc.lastKey = it
        }
        transactionViewModel.calcOperation.observe(viewLifecycleOwner) {
            calc.lastOperation = it
        }
        transactionViewModel.calcNewFormula.observe(viewLifecycleOwner) { formula ->
            binding.formula.text = formula
        }
        transactionViewModel.transactionNominal.observe(viewLifecycleOwner) { result ->
            binding.result.text = result
            calc.setInputDisplayedFormula(result)
        }
    }

    override fun showNewResult(value: String, context: Context) {
        transactionViewModel._transactionNominal.value = value
    }

    override fun showNewFormula(value: String, context: Context) {
        transactionViewModel._calcNewFormula.value = value
    }

    @SuppressLint("SetTextI18n")
    fun setupCategory() {
        /**
         * Category View Binding Section
         */
        binding.categoryName.setOnClickListener {
            /**
             * Close keyboard before navigate
             */
            val imm =
                getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
            Navigation.findNavController(requireView())
                .navigate(
                    AddEditTransactionFragmentDirections.actionAddTransactionFragmentToAddCategoryTranscFragment(
                        transactionViewModel.transactionType.value!!
                    )
                )
        }
        if (transactionViewModel.categoryName.value == null) {
            binding.categoryName.text = "Category Not Selected"
        }
        transactionViewModel.categoryName.observe(viewLifecycleOwner) { result ->
            binding.categoryName.text = result ?: "Category Not Selected"
        }

    }

    fun setupDateTransaction() {
        /**
         * Date Transaction View Binding Section
         */
        if (transactionViewModel.dateTransaction.value == null) {
            saveDatePickerToLiveData(CurrentDate.day, CurrentDate.month, CurrentDate.year)
        }
        binding.dateTransaction.setOnClickListener {
            showDatePicker(CurrentDate)
                .show(
                    requireActivity().supportFragmentManager,
                    "Datepickerdialog"
                )
        }
        transactionViewModel.dateTransaction.observe(viewLifecycleOwner) { date ->
            val dateTransaction = CurrentDate.getDateString(date)
            binding.dateTransaction.text = "$dateTransaction"
        }
    }

    fun showDatePicker(currentDate: CurrentDate): DatePickerDialog {
        val datePickerDialog =
            DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
                saveDatePickerToLiveData(dayOfMonth, monthOfYear, year)
            }, currentDate.year, currentDate.month, currentDate.day)

        return datePickerDialog
    }

    fun saveDatePickerToLiveData(dayOfMonth: Int, monthOfYear: Int, year: Int) {
        val realMonth = monthOfYear + 1
        val date = CurrentDate
            .dateFormat("dd MM yyyy")
            .parse("$dayOfMonth $realMonth $year")
        Log.i(TAG, "Saving date into live data $date")
        transactionViewModel._dateTransaction.value = date
    }

    fun setupNoteTransaction() {
        /**
         * Note Transaction View Binding Section
         */
        transactionViewModel.dataLoading.observe(viewLifecycleOwner) { loading ->
            if (loading == false) {
                binding.noteTransc.setText(transactionViewModel.noteTransaction.value)
            }
        }
        binding.noteTransc.addTextChangedListener {
            transactionViewModel.editTextNoteTransactionChanged(it.toString())
        }
    }

    private fun AlertDialog.Builder.setEditText(editText: EditText): AlertDialog.Builder {
        val container = FrameLayout(context)
        container.addView(editText)
        val containerParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val marginHorizontal = 48F
        val marginTop = 16F
        containerParams.topMargin = (marginTop / 2).toPx
        containerParams.leftMargin = marginHorizontal.toInt()
        containerParams.rightMargin = marginHorizontal.toInt()
        container.layoutParams = containerParams

        val superContainer = FrameLayout(context)
        superContainer.addView(container)

        setView(superContainer)

        return this
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_action) {
            calc.handleEquals()
            transactionViewModel.validateTransaction()

            transactionViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
            transactionViewModel.navigateToHome.observe(viewLifecycleOwner, EventObserver {
                val action =
                    AddEditTransactionFragmentDirections.actionAddTransaksiFragmentToHomeFragment()
                findNavController().navigate(action)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.i(TAG, "AddTransactionFragment destroyed")
    }

}