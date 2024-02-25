package com.example.project4
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import android.widget.EditText

class AddCityDialog(private val onCityAdded: (String) -> Unit) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = EditText(requireContext())
        return AlertDialog.Builder(requireContext())
            .setTitle("Добавить город")
            .setView(editText)
            .setPositiveButton("Добавить") { _, _ ->
                onCityAdded(editText.text.toString())
            }
            .setNegativeButton("Отмена", null)
            .create()
    }
}
