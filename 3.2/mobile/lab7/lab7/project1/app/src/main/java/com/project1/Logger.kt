package com.project1

import android.util.Log

object Logger {
    private const val TAG = "MusicApp"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}