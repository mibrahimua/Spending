package com.mibrahimuadev.spending.ui.transaction

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentCalculatorBinding
import kotlinx.android.synthetic.main.fragment_calculator.*
import net.objecthunter.exp4j.ExpressionBuilder


class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private lateinit var addTransaksiViewModel: AddTransaksiViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val application = requireNotNull(this.activity).application
        _binding = FragmentCalculatorBinding.inflate(layoutInflater)

        // Create instance to ViewModel Factory
        val viewModelFactory = AddTransaksiViewModelFactory(application)

        // Get a reference to the ViewModel associated with this fragment.
        addTransaksiViewModel =
            ViewModelProvider(this, viewModelFactory).get(AddTransaksiViewModel::class.java)

        binding.tvExpression.setMovementMethod(ScrollingMovementMethod());
        // Numbers
        binding.tvOne.setOnClickListener { appendOnExpression("1", true) }
        binding.tvTwo.setOnClickListener { appendOnExpression("2", true) }
        binding.tvThree.setOnClickListener { appendOnExpression("3", true) }
        binding.tvFour.setOnClickListener { appendOnExpression("4", true) }
        binding.tvFive.setOnClickListener { appendOnExpression("5", true) }
        binding.tvSix.setOnClickListener { appendOnExpression("6", true) }
        binding.tvSeven.setOnClickListener { appendOnExpression("7", true) }
        binding.tvEight.setOnClickListener { appendOnExpression("8", true) }
        binding.tvNine.setOnClickListener { appendOnExpression("9", true) }
        binding.tvZero.setOnClickListener { appendOnExpression("0", true) }
        binding.tvDot.setOnClickListener { appendOnExpression(".", true) }

        // Operators
        binding.tvPlus.setOnClickListener { appendOnExpression("+", false) }
        binding.tvMinus.setOnClickListener { appendOnExpression("-", false) }

        binding.tvBack.setOnClickListener {
            val string = binding.tvExpression.text.toString()
            if (string.isNotEmpty()) {
                tvExpression.text = string.substring(0, string.length - 1)
            }
        }
        binding.tvBack.setOnLongClickListener {
            val string = binding.tvExpression.text.toString()
            if (string.isNotEmpty() && binding.tvEquals.text.toString() != resources.getString(R.string.number_submit)) {
                tvExpression.text = ""
            }
            true
        }

        binding.tvEquals.setOnClickListener {
            try {
                val expression = ExpressionBuilder(binding.tvExpression.text.toString()).build()
                val result = expression.evaluate()
                val longResult = result.toLong()
                if (result == longResult.toDouble()) {
                    binding.tvExpression.text = longResult.toString()
                } else {
                    binding.tvExpression.text = result.toString()
                }
                binding.tvEquals.text = resources.getString(R.string.number_submit)
            } catch (e: Exception) {
                Log.d("Exception", " message : " + e.message)
            }
        }

        return binding.root
    }

    fun appendOnExpression(string: String, canClear: Boolean) {

        if (canClear) {
            binding.tvExpression.append(string)
//            addTransaksiViewModel._dataCalculator.value = string.toDouble()
        } else {
            val operator = binding.tvExpression.text.takeLast(1).toString()
            if (operator != "-" && operator != "+") {
                binding.tvExpression.append(string)
                binding.tvEquals.text = resources.getString(R.string.number_equal)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}