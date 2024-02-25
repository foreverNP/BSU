package com.example.project5

import android.gesture.Gesture
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.Prediction
import android.widget.EditText


class MainActivity : AppCompatActivity(), GestureOverlayView.OnGesturePerformedListener {

    private var gLib: GestureLibrary? = null
    private var gestures: GestureOverlayView? = null
    private lateinit var etText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etText = findViewById(R.id.etText)

        gLib = GestureLibraries.fromRawResource(this, R.raw.gesture).apply {
            if (!load()) finish()
        }

        gestures = findViewById<GestureOverlayView>(R.id.gestures).apply {
            addOnGesturePerformedListener(this@MainActivity)
        }
    }

    override fun onGesturePerformed(overlay: GestureOverlayView?, gesture: Gesture?) {
        val predictions = gLib?.recognize(gesture) ?: return

        if (predictions.isNotEmpty()) {
            val prediction = predictions[0]
            when (prediction.name) {
                "delete" -> {
                    etText.text.delete(etText.text.length - 1, etText.text.length)
                }
                "\\n" -> {
                    etText.append("\n")
                }
                else -> {
                    etText.append(prediction.name)
                }
            }
        }
    }
}
