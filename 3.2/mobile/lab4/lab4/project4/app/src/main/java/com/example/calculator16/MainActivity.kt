package com.example.calculator16

import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.Prediction
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator16.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), GestureOverlayView.OnGesturePerformedListener {

    private lateinit var binding: ActivityMainBinding
    private var currentInput: String = ""
    private lateinit var gestureLibrary: GestureLibrary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture)
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show()
            finish()
        }

        val gestureOverlay = binding.gestureOverlay
        gestureOverlay.addOnGesturePerformedListener(this)

        setButtonListeners()
    }

    private fun setButtonListeners() {
        binding.btnClear.setOnClickListener { clearInput() }
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

    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {
        val predictions: ArrayList<Prediction> = gestureLibrary.recognize(gesture)
        if (predictions.isNotEmpty()) {
            val prediction = predictions[0]
            if (prediction.score > 1.0) {
                when (prediction.name) {
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                        appendToInput(prediction.name)
                    }
                    "dot" -> {
                        appendToInput(".")
                    }
                    "sign" -> {
                        toggleSign()
                    }
                    else -> {
                        Toast.makeText(this, "Жест не распознан", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
