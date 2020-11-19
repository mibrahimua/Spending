package com.mibrahimuadev.spending.ui.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mibrahimuadev.spending.databinding.FragmentAddTransaksiBinding
import kotlinx.android.synthetic.main.fragment_add_transaksi.*

class AddTransaksiFragment : Fragment(), Calculator {

    lateinit var calc: CalculatorImpl
    private var _binding: FragmentAddTransaksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var addTransaksiViewModel: AddTransaksiViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        _binding = FragmentAddTransaksiBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        calc = CalculatorImpl(this, application)

        binding.btnPlus.setOnClickListener { calc.handleOperation(PLUS); }
        binding.btnMinus.setOnClickListener { calc.handleOperation(MINUS); }

        binding.btnClear.setOnClickListener { calc.handleClear(); }
        binding.btnClear.setOnLongClickListener { calc.handleReset(); true }

        getButtonIds().forEach {
            it.setOnClickListener { calc.numpadClicked(it.id); }
        }

        binding.btnEquals.setOnClickListener { calc.handleEquals(); }
//        binding.result.setOnLongClickListener { copyToClipboard(true) }

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

//    private fun copyToClipboard(copyResult: Boolean): Boolean {
//        var value = result.value
//        if (copyResult) {
//            value = result.value
//        }
//
//        return if (value.isEmpty()) {
//            false
//        } else {
//            copyToClipboard(value)
//            true
//        }
//    }

    override fun showNewResult(value: String, context: Context) {
        binding.result.text = value
    }

    override fun showNewFormula(value: String, context: Context) {
        binding.formula.text = value
    }
}