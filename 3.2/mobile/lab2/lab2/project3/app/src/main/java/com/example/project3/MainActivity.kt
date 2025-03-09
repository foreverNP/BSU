package com.example.project3

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var started = false
    private var number = 0
    private var tries = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchSavedInstanceData(savedInstanceState)
        binding.doGuess.isEnabled = started

        binding.startBtn.setOnClickListener { start(it) }
        binding.doGuess.setOnClickListener { guess(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        putInstanceData(outState)
    }

    fun start(v: View) {
        log("Game started")
        binding.num.setText("")
        started = true
        binding.doGuess.isEnabled = true
        binding.status.text = getString(R.string.guess_hint, 1, 200)
        number = (1..200).random()
        tries = 0
    }

    fun guess(v: View) {
        val input = binding.num.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_no_input), Toast.LENGTH_SHORT).show()
            return
        }

        val userGuess: Int
        try {
            userGuess = input.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, getString(R.string.error_not_a_number), Toast.LENGTH_SHORT).show()
            return
        }

        if (userGuess < 1 || userGuess > 200) {
            Toast.makeText(this, getString(R.string.error_out_of_range), Toast.LENGTH_SHORT).show()
            return
        }

        tries++
        log("Try $tries: input $userGuess")
        when {
            userGuess < number -> {
                binding.status.setText(R.string.status_too_low)
                binding.num.setText("")
            }
            userGuess > number -> {
                binding.status.setText(R.string.status_too_high)
                binding.num.setText("")
            }
            else -> {
                binding.status.text = getString(R.string.status_hit, tries)
                started = false
                binding.doGuess.isEnabled = false
            }
        }
    }

    private fun putInstanceData(outState: Bundle) {
        outState.putBoolean("started", started)
        outState.putInt("number", number)
        outState.putInt("tries", tries)
        outState.putString("statusMsg", binding.status.text.toString())
        outState.putStringArrayList("logs", ArrayList(binding.console.text.split("\n")))
    }

    private fun fetchSavedInstanceData(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            started = it.getBoolean("started")
            number = it.getInt("number")
            tries = it.getInt("tries")
            binding.status.text = it.getString("statusMsg")
            val logs = it.getStringArrayList("logs")
            if (logs != null) {
                binding.console.text = logs.joinToString("\n")
            }
        }
    }

    private fun log(msg: String) {
        Log.d("LOG", msg)
        binding.console.log(msg)
    }
}
