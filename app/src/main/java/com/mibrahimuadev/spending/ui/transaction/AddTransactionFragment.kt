package com.mibrahimuadev.spending.ui.transaction

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentAddTransactionBinding
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.EventObserver
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class AddTransactionFragment : Fragment(), Calculator {
    private val TAG = "AddTransactionFragment"
    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.nav_transc) {
        TransactionViewModelFactory(requireActivity().application)
    }
    private val args: AddTransactionFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "AddTransactionFragment created")

        /**
         * Set title fragment depend on transaction type (Expense, Income)
         */
//        (activity as AppCompatActivity).supportActionBar?.title = args.transactionType

        val application = requireNotNull(this.activity).application
        _binding = FragmentAddTransactionBinding.inflate(layoutInflater)

        /**
         * Prevent layout to adjust when user use keyboard
         */
        getActivity()?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        /**
         * Init Transaction
         */
        transactionViewModel.actionTypeArgs = args.actionType
        transactionViewModel.transactionTypeArgs = TransactionType.valueOf(args.transactionType!!)
        transactionViewModel.transactionIdArgs = args.transactionId
        transactionViewModel.categoryIdArgs = args.categoryId
        transactionViewModel.categoryNameArgs = args.categoryName
        runBlocking {
            Log.i(TAG, "Coroutine cek if category exist starting")
            val job = lifecycleScope.launch(Dispatchers.IO) {
                transactionViewModel.isCategoryExist(args.categoryId)
            }
            job.join()
            Log.i(TAG, "Coroutine ends " + job.isActive)
        }
        transactionViewModel.startTransaction()

        /**
         *  Transaction Type View Binding Section
         */
        transactionViewModel.transactionType.observe(viewLifecycleOwner) {

            if(it == TransactionType.EXPENSE) {
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
                    binding.radioIncome.setTextColor(Color.BLUE)
                    transactionType = TransactionType.EXPENSE
                }
                R.id.radioIncome -> {
                    binding.radioIncome.setTextColor(Color.WHITE)
                    binding.radioExpense.setTextColor(Color.BLUE)
                    transactionType = TransactionType.INCOME
                }
            }
            if(transactionViewModel.transactionType.value != transactionType) {
                transactionViewModel.resetCategory()
            }
            transactionViewModel._transactionType.value = transactionType
        }

        /**
         * Calculator View Binding Section
         */
        calc = CalculatorImpl(this, application)
        binding.btnPlus.setOnClickListener {
            calc.handleOperation(PLUS)
            transactionViewModel._calcOperation.value = calc.lastOperation
        }
        binding.btnMinus.setOnClickListener {
            calc.handleOperation(MINUS)
            transactionViewModel._calcOperation.value = calc.lastOperation
        }
        binding.btnMultiply.setOnClickListener {
            calc.handleOperation(MULTIPLY)
            transactionViewModel._calcOperation.value = calc.lastOperation
        }
        binding.btnDivide.setOnClickListener {
            calc.handleOperation(DIVIDE)
            transactionViewModel._calcOperation.value = calc.lastOperation
        }
        binding.btnClear.setOnClickListener { calc.handleClear(); }
        binding.btnClear.setOnLongClickListener { calc.handleReset(); true }
        getNumberButtonIds().forEach {
            it.setOnClickListener {
                calc.numpadClicked(it.id);
                transactionViewModel._calcLastKey.value = calc.lastKey
            }
        }
        binding.btnEquals.setOnClickListener { calc.handleEquals(); }
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

        /**
         * Category View Binding Section
         */
        binding.btnCategory.setOnClickListener {
            val imm =
                getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
            Navigation.findNavController(requireView())
                .navigate(
                    AddTransactionFragmentDirections.actionAddTransactionFragmentToAddCategoryTranscFragment(
                        transactionViewModel.transactionType.value!!
                    )
                )
        }
        transactionViewModel.categoryName.observe(viewLifecycleOwner) { result ->
            binding.categoryName.text = result ?: "Category Not Selected"
        }


        /**
         * Date Transaction View Binding Section
         */
        if (transactionViewModel.dateTransaction.value == null) {
            saveDatePickerToLiveData(CurrentDate.day, CurrentDate.month, CurrentDate.year)
        }
        binding.btnDate.setOnClickListener {
            showDatePicker(CurrentDate)
                .show(
                    requireActivity().supportFragmentManager,
                    "Datepickerdialog"
                )
        }
        transactionViewModel.dateTransaction.observe(viewLifecycleOwner) { date ->
            val dateTransaction = CurrentDate.getDateString(date)
            binding.textDate.text = "$dateTransaction"
        }

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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_transc_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.save_action) {
            calc.handleEquals()
            transactionViewModel.validateTransaction()
            transactionViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
            transactionViewModel.navigateToHome.observe(viewLifecycleOwner, EventObserver {
                val action =
                    AddTransactionFragmentDirections.actionAddTransaksiFragmentToHomeFragment()
                findNavController().navigate(action)
            })
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
        transactionViewModel._transactionNominal.value = value
    }

    override fun showNewFormula(value: String, context: Context) {
        transactionViewModel._calcNewFormula.value = value
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

    override fun onDestroyView() {

        super.onDestroyView()

        Log.i(TAG, "AddTransactionFragment destroyed")
    }
}