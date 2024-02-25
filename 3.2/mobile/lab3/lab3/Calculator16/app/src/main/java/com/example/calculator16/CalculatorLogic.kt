package com.example.calculator16

import java.text.DecimalFormat

class CalculatorLogic {

    fun convertArea(input: String, isUK: Boolean): String {
        val inputValue = input.toDoubleOrNull() ?: return "Ошибка ввода"
        val result = if (isUK) {
            inputValue * 1.19599
        } else {
            inputValue * 10.7639
        }
        return DecimalFormat("#.##").format(result)
    }

    fun toggleSign(input: String): String {
        return if (input.startsWith("-"))
            input.removePrefix("-")
        else
            if (input.isNotEmpty()) "-$input" else input
    }
}
