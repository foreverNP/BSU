package com.example.project3

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import android.widget.TextView

class Console(ctx: Context, aset: AttributeSet? = null) : ScrollView(ctx, aset) {
    private val tv = TextView(ctx)
    var text: String
        get() = tv.text.toString()
        set(value) { tv.text = value }
    init {
        setBackgroundColor(0x40FFFF00.toInt())
        addView(tv)
    }
    fun log(msg: String) {
        val lines = if (tv.text.toString().isEmpty()) listOf() else tv.text.toString().split("\n")
        val newLines = lines.takeLast(100) + msg
        tv.text = newLines.joinToString("\n")
        post {
            fullScroll(FOCUS_DOWN)
        }
    }
}
