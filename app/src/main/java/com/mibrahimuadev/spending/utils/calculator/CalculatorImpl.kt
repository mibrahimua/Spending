package com.mibrahimuadev.spending.utils.calculator

import android.content.Context
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.ui.transaction.*
import com.mibrahimuadev.spending.utils.Formatter
import com.mibrahimuadev.spending.utils.format
import net.objecthunter.exp4j.ExpressionBuilder
import org.jetbrains.anko.toast

class CalculatorImpl(calculator: Calculator, private val context: Context) {
    private var callback: Calculator? = calculator
    private var baseValue = 0.0
    private var secondValue = 0.0
    private var inputDisplayedFormula = "0"
    var lastKey = ""
    var lastOperation = ""
    private var operations = listOf("+", "-", "*", "/")
    private var operationsRegex = "[-+*/]".toPattern()
    private var numbersRegex = "[^0-9,.]".toRegex()

    private fun addDigit(number: Int) {
        if (inputDisplayedFormula == "0") {
            inputDisplayedFormula = ""
        }

        inputDisplayedFormula += number
        addThousandsDelimiter()
        showNewResult(inputDisplayedFormula)
    }

    private fun zeroClicked() {
        val valueToCheck = inputDisplayedFormula.trimStart('-').replace(",", "")
        val value = valueToCheck.substring(valueToCheck.indexOfAny(operations) + 1)
        if (value != "0" || value.contains(".")) {
            addDigit(0)
        }
    }

    private fun decimalClicked() {
        val valueToCheck = inputDisplayedFormula.trimStart('-').replace(",", "")
        val value = valueToCheck.substring(valueToCheck.indexOfAny(operations) + 1)
        if (!value.contains(".")) {
            when {
                value == "0" && !valueToCheck.contains(operationsRegex.toRegex()) -> inputDisplayedFormula =
                    "0"
                value == "" -> inputDisplayedFormula += "0."
                else -> inputDisplayedFormula += "."
            }
        }

        lastKey = DECIMAL
        showNewResult(inputDisplayedFormula)
    }

    private fun addThousandsDelimiter() {
        val valueToCheck =
            numbersRegex.split(inputDisplayedFormula).filter { it.trim().isNotEmpty() }
        valueToCheck.forEach {
            var newString = Formatter.addGroupingSeparators(it)
            // allow writing numbers like 0.0003
            if (it.contains(".")) {
                newString = newString.substringBefore(".") + ".${it.substringAfter(".")}"
            }

            inputDisplayedFormula = inputDisplayedFormula.replace(it, newString)
        }
    }

    fun setInputDisplayedFormula(operation: String) {
        inputDisplayedFormula = operation
    }

    fun handleOperation(operation: String) {
        if (inputDisplayedFormula == "") {
            inputDisplayedFormula = "0"
        }

        if (operation == ROOT && inputDisplayedFormula == "0") {
            inputDisplayedFormula = "√"
        }

        val lastChar = inputDisplayedFormula.last().toString()
        if (lastChar == ".") {
            inputDisplayedFormula = inputDisplayedFormula.dropLast(1)
        } else if (operations.contains(lastChar)/* || lastchar == "."*/) {
            inputDisplayedFormula = inputDisplayedFormula.dropLast(1)
            inputDisplayedFormula += getSign(operation)
        } else if (!inputDisplayedFormula.trimStart('-').contains(operationsRegex.toRegex())) {
            inputDisplayedFormula += getSign(operation)
        }

        if (lastKey == DIGIT || lastKey == DECIMAL) {
            if (lastOperation != "" && operation == PERCENT) {
                handlePercent()
            } else {
                secondValue = getSecondValue()
                calculateResult()
                if (!operations.contains(inputDisplayedFormula.last().toString())) {
                    inputDisplayedFormula += getSign(operation)
                }
            }
        }

        lastKey = operation
        lastOperation = operation
        showNewResult(inputDisplayedFormula)
    }

    // handle percents manually, it doesn't seem to be possible view net.objecthunter:exp4j. "%" is used only for module there

    private fun handlePercent() {
        val result = calculatePercentage(baseValue, getSecondValue(), lastOperation)
        showNewFormula("${baseValue.format()}${getSign(lastOperation)}${getSecondValue().format()}%")
        inputDisplayedFormula = result.format()
        showNewResult(result.format())
        baseValue = result
    }

    fun handleEquals() {
//        if (lastKey == EQUALS) {
//            calculateResult()
//        }

        if (lastKey != DIGIT && lastKey != DECIMAL) {
//            context.toast(R.string.invalid_format_used)
            return
        }

        secondValue = getSecondValue()
        calculateResult()
        lastKey = EQUALS
    }

    private fun getSecondValue(): Double {
        val valueToCheck = inputDisplayedFormula.trimStart('-').replace(",", "")
        var value = valueToCheck.substring(valueToCheck.indexOfAny(operations) + 1)
        if (value == "") {
            value = "0"
        }

        return value.toDouble()
    }

    private fun calculateResult() {
        if (lastOperation == ROOT && inputDisplayedFormula.startsWith("√")) {
            baseValue = 1.0
        }

        if (lastKey != EQUALS) {
            val valueToCheck = inputDisplayedFormula.trimStart('-').replace(",", "")
            val parts = valueToCheck.split(operationsRegex).filter { it != "" }
            baseValue = Formatter.stringToDouble(parts.first())
            if (inputDisplayedFormula.startsWith("-")) {
                baseValue *= -1
            }

            secondValue = parts.getOrNull(1)?.replace(",", "")?.toDouble() ?: secondValue
        }

        if (lastOperation != "") {
            val expression =
                "${baseValue.format()}${getSign(lastOperation)}${secondValue.format()}".replace(
                    "√",
                    "sqrt"
                )
            try {
                val result = ExpressionBuilder(expression.replace(",", "")).build().evaluate()
                showNewResult(result.format())
                baseValue = result
                inputDisplayedFormula = result.format()
                showNewFormula(expression.replace("sqrt", "√"))
            } catch (e: Exception) {
                context.toast(R.string.unknown_error_occurred)
            }
        }
    }

    private fun calculatePercentage(baseValue: Double, secondValue: Double, sign: String): Double {
        return when (sign) {
            MULTIPLY -> {
                val partial = 100 / secondValue
                baseValue / partial
            }
            DIVIDE -> {
                val partial = 100 / secondValue
                baseValue * partial
            }
            PLUS -> {
                val partial = baseValue / (100 / secondValue)
                baseValue.plus(partial)
            }
            MINUS -> {
                val partial = baseValue / (100 / secondValue)
                baseValue.minus(partial)
            }
            else -> baseValue / (100 * secondValue)
        }
    }

    private fun showNewResult(value: String) {
        callback!!.showNewResult(value, context)
    }

    private fun showNewFormula(value: String) {
        callback!!.showNewFormula(value, context)
    }

    fun handleClear() {
        var newValue = inputDisplayedFormula.dropLast(1)
        if (newValue == "") {
            newValue = "0"
        }

        newValue = newValue.trimEnd(',')
        inputDisplayedFormula = newValue
        addThousandsDelimiter()
        showNewResult(inputDisplayedFormula)
    }

    fun handleReset() {
        resetValues()
        showNewResult("0")
        showNewFormula("")
        inputDisplayedFormula = ""
    }

    private fun resetValues() {
        baseValue = 0.0
        secondValue = 0.0
        lastKey = ""
        lastOperation = ""
    }

    private fun getSign(lastOperation: String) = when (lastOperation) {
        MINUS -> "-"
        MULTIPLY -> "*"
        DIVIDE -> "/"
        PERCENT -> "%"
        POWER -> "^"
        ROOT -> "√"
        else -> "+"
    }

    fun numpadClicked(id: Int) {
        if (lastKey == EQUALS) {
            lastOperation = EQUALS
        }

        lastKey = DIGIT

        when (id) {
            R.id.btnDecimal -> decimalClicked()
            R.id.btn_0 -> zeroClicked()
            R.id.btn1 -> addDigit(1)
            R.id.btn2 -> addDigit(2)
            R.id.btn3 -> addDigit(3)
            R.id.btn4 -> addDigit(4)
            R.id.btn5 -> addDigit(5)
            R.id.btn6 -> addDigit(6)
            R.id.btn7 -> addDigit(7)
            R.id.btn8 -> addDigit(8)
            R.id.btn9 -> addDigit(9)
        }
    }


}