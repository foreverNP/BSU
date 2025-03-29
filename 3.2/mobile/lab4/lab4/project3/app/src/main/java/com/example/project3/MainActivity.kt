package com.example.project3

import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), GestureOverlayView.OnGesturePerformedListener {

    private lateinit var tvInfo: TextView
    private lateinit var etInput: EditText
    private lateinit var bControl: Button
    private var targetNumber: Int = 0
    private var gameFinished: Boolean = false
    private lateinit var gestureLibrary: GestureLibrary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInfo = findViewById(R.id.textView1)
        etInput = findViewById(R.id.input_value)
        bControl = findViewById(R.id.submit)

        targetNumber = (1..100).random()
        gameFinished = false

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
        if (!gestureLibrary.load()) {
            finish()
        }

        val gestureOverlayView = findViewById<GestureOverlayView>(R.id.gestureOverlay)
        gestureOverlayView.addOnGesturePerformedListener(this)
    }
    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {
        val predictions = gestureLibrary.recognize(gesture)
        if (predictions.isNotEmpty()) {
            val prediction = predictions[0]
            if (prediction.score > 1.0) {
                when (prediction.name) {
                    "0" -> processDigit(0)
                    "1" -> processDigit(1)
                    "2" -> processDigit(2)
                    "3" -> processDigit(3)
                    "4" -> processDigit(4)
                    "5" -> processDigit(5)
                    "6" -> processDigit(6)
                    "7" -> processDigit(7)
                    "8" -> processDigit(8)
                    "9" -> processDigit(9)
                    "stop" -> finalizeInput()
                    else -> tvInfo.text = resources.getString(R.string.error)
                }
            }
        }
    }

    private fun processDigit(digit: Int) {
        etInput.append(digit.toString())
    }

    private fun finalizeInput() {
        onClick(bControl)
    }


    fun onClick(v: View) {
        try {
            if (!gameFinished) {
                val inputText = etInput.text.toString()
                if (inputText.isEmpty()) {
                    tvInfo.text = resources.getString(R.string.error)
                }

                val inputNumber = inputText.toInt()
                when {
                    inputNumber < 1 || inputNumber > 100 -> {
                        tvInfo.text = resources.getString(R.string.error)
                    }
                    inputNumber < targetNumber -> {
                        tvInfo.text = resources.getString(R.string.behind)
                    }
                    inputNumber > targetNumber -> {
                        tvInfo.text = resources.getString(R.string.ahead)
                    }
                    else -> {
                        tvInfo.text = resources.getString(R.string.hit)
                        bControl.text = resources.getString(R.string.play_more)
                        gameFinished = true
                    }
                }
            } else {
                targetNumber = (1..100).random()
                tvInfo.text = resources.getString(R.string.try_to_guess)
                bControl.text = resources.getString(R.string.input_value)
                gameFinished = false
            }
        } catch (e: NumberFormatException) {
            tvInfo.text = resources.getString(R.string.error)
        }

        etInput.setText("")
    }
}
