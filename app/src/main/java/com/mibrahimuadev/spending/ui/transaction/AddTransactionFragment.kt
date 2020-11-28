package com.mibrahimuadev.spending.ui.transaction

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
import com.mibrahimuadev.spending.utils.DateFormatter
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddTransactionFragment : Fragment(), Calculator {

    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val addTransactionViewModel: AddTransactionViewModel by navGraphViewModels(R.id.nav_add_transc) {
        AddTransactionViewModelFactory(requireActivity().application)
    }
    private val args: AddTransactionFragmentArgs by navArgs()
    @RequiresApi(Build.VERSION_CODES.O)
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

        val viewModelFactory = CategoryViewModelFactory(application)

        val categoryViewModel =
            ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

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
                .navigate(
                    AddTransactionFragmentDirections.actionAddTransaksiFragmentToAddCategoryTranscFragment(
                        args.transactionType
                    )
                )
        }

        addTransactionViewModel.calcNewFormula.observe(viewLifecycleOwner) { formula ->
            binding.formula.text = formula
        }

        addTransactionViewModel.calcNewResult.observe(viewLifecycleOwner) { result ->
            binding.result.text = result
            calc.setInputDisplayedFormula(result)
        }

        categoryViewModel.getCategory(args.idKategori)
        categoryViewModel.categoryName.observe(viewLifecycleOwner) { result ->
            binding.categoryName.text = result ?: "Category Not Selected"
        }
        addTransactionViewModel._transactionType.value = args.transactionType
        addTransactionViewModel._transactionCategory.value = args.idKategori

        val now = Calendar.getInstance()
        val currentYear: Int = now.get(Calendar.YEAR)
        val currentMonth: Int = now.get(Calendar.MONTH)
        val currentDay: Int = now.get(Calendar.DAY_OF_MONTH)
        val monthName = listOf(
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
        )

        binding.textDate.text = "$currentDay " + monthName[currentMonth] + " $currentYear"
        val simpleDateFormat = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
        val date = simpleDateFormat.parse("$currentDay $currentMonth $currentYear")
        addTransactionViewModel._dateTransaction.value = date
        val datePickerDialog =
            DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->

                binding.textDate.text = "$dayOfMonth " + monthName[monthOfYear] + " $year"
                val date = simpleDateFormat.parse("$dayOfMonth $monthOfYear $year")
                addTransactionViewModel._dateTransaction.value = date
            }, currentYear, currentMonth, currentDay)

        binding.btnDate.setOnClickListener {
//            datePickerDialog.setTitle("INI JUDUL")
//            datePickerDialog.setOkText("SIP")
//            datePickerDialog.setCancelText("GA JADI")
            datePickerDialog.show(requireActivity().supportFragmentManager, "Datepickerdialog")
        }
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
        if(id == R.id.save_action){

            addTransactionViewModel.saveTransaction()
//            Navigation.findNavController(requireView()).navigate(AddTransactionFragmentDirections.actionAddTransaksiFragmentToHomeFragment())
            Toast.makeText(requireContext(), "save action clicked", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
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
        addTransactionViewModel._calcNewResult.value = value
    }

    override fun showNewFormula(value: String, context: Context) {
        addTransactionViewModel._calcNewFormula.value = value

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("AddTransactionFragment", "AddTransactionFragment destroyed")
    }
}