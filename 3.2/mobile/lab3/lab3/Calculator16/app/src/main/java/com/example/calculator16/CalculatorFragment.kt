package com.example.calculator16

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.calculator16.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private var currentInput: String = ""
    private val logic = CalculatorLogic()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
    }

    private fun setButtonListeners() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )
        for (btn in numberButtons) {
            btn.setOnClickListener { view ->
                view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate))
                appendToInput(btn.text.toString())
            }
        }
        binding.btnDot.setOnClickListener { appendToInput(".") }

        binding.btnSign.setOnClickListener {
            currentInput = logic.toggleSign(currentInput)
            updateDisplay(currentInput)
        }

        binding.btnClear.setOnClickListener { clearInput() }

        binding.btnConvertUK.setOnClickListener { convertArea(isUK = true) }
        binding.btnConvertUS.setOnClickListener { convertArea(isUK = false) }

        binding.checkAdvanced.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                if (isChecked) getString(R.string.advanced_mode_on)
                else getString(R.string.advanced_mode_off),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnInfo.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, InfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun appendToInput(value: String) {
        if (value == "." && currentInput.contains(".")) return
        currentInput += value
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
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.error_no_input))
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
            return
        }
        val resultText = logic.convertArea(currentInput, isUK)
        val unitName = if (isUK) getString(R.string.unit_uk) else getString(R.string.unit_us)
        updateDisplay("$resultText $unitName")

        currentInput = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
