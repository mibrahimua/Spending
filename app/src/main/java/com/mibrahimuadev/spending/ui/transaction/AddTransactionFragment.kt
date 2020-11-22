package com.mibrahimuadev.spending.ui.transaction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mibrahimuadev.spending.databinding.FragmentAddTransactionBinding

class AddTransactionFragment : Fragment(), Calculator {

    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var addTransactionViewModel: AddTransactionViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        _binding = FragmentAddTransactionBinding.inflate(layoutInflater)

        getActivity()?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        calc = CalculatorImpl(this, application)

        binding.btnPlus.setOnClickListener { calc.handleOperation(PLUS); }
        binding.btnMinus.setOnClickListener { calc.handleOperation(MINUS); }

        binding.btnClear.setOnClickListener { calc.handleClear(); }
        binding.btnClear.setOnLongClickListener { calc.handleReset(); true }
        getButtonIds().forEach {
            it.setOnClickListener { calc.numpadClicked(it.id); }
        }

        binding.btnEquals.setOnClickListener { calc.handleEquals(); }
        binding.result.setOnLongClickListener { copyToClipboard(true) }

        binding.btnCategory.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(AddTransactionFragmentDirections.actionAddTransaksiFragmentToAddCategoryTranscFragment())
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
        binding.result.text = value
    }

    override fun showNewFormula(value: String, context: Context) {
        binding.formula.text = value
    }
}