package com.example.calculator16

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator16.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonListeners()
    }

    private fun setButtonListeners() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )
        for (btn in numberButtons) {
            btn.setOnClickListener { appendToInput(btn.text.toString()) }
        }
        binding.btnDot.setOnClickListener { appendToInput(".") }
        // Смена знака
        binding.btnSign.setOnClickListener { toggleSign() }
        // Очистка ввода
        binding.btnClear.setOnClickListener { clearInput() }

        // Обработчики конвертации
        binding.btnConvertUK.setOnClickListener { convertArea(isUK = true) }
        binding.btnConvertUS.setOnClickListener { convertArea(isUK = false) }
    }

    private fun appendToInput(value: String) {
        if (value == "." && currentInput.contains(".")) return
        currentInput += value
        updateDisplay(currentInput)
    }

    private fun toggleSign() {
        currentInput = if (currentInput.startsWith("-")) {
            currentInput.removePrefix("-")
        } else {
            if (currentInput.isNotEmpty()) "-$currentInput" else currentInput
        }
        updateDisplay(currentInput)
    }

    private fun clearInput() {
        currentInput = ""
        updateDisplay("0")
    }

    private fun updateDisplay(text: String) {
        binding.tvDisplay.text = text
    }

    private fun convertArea(isUK: Boolean) {
        if (currentInput.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_no_input), Toast.LENGTH_SHORT).show()
            return
        }
        val inputValue: Double
        try {
            inputValue = currentInput.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, getString(R.string.error_not_a_number), Toast.LENGTH_SHORT).show()
            return
        }

        val result = if (isUK) {
            // 1 m² = 1.19599 квадратных ярдов
            inputValue * 1.19599
        } else {
            // 1 m² = 10.7639 квадратных футов
            inputValue * 10.7639
        }

        val formatted = DecimalFormat("#.##").format(result)
        val unitName = if (isUK) getString(R.string.unit_uk) else getString(R.string.unit_us)
        updateDisplay("$formatted $unitName")
        currentInput = ""
    }
}
