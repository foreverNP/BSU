package com.mockcrypto.data.remote

import android.content.Context
import android.util.Log
import java.util.Properties
import java.io.FileInputStream
import java.io.IOException

object ApiKeyConfig {
    private const val LOCAL_PROPERTIES_FILENAME = "local.properties"
    private const val COINGECKO_API_KEY = "coingecko.api.key"
    private const val TAG = "ApiKeyConfig"

    /**
     * Gets the CoinGecko API key from local.properties
     * Add the following line to your local.properties file:
     * coingecko.api.key=YOUR-ACTUAL-API-KEY
     */
    fun getCoinGeckoApiKey(context: Context): String {
        try {
            val properties = Properties()
            val inputStream = FileInputStream(context.filesDir.resolve(LOCAL_PROPERTIES_FILENAME))
            properties.load(inputStream)
            val key = properties.getProperty(COINGECKO_API_KEY)
            if (!key.isNullOrEmpty()) {
                Log.d(TAG, "Found API key in app files directory")
                return key
            }
        } catch (e: IOException) {
            Log.d(TAG, "API key not found in app files: ${e.message}")
        }
        
        try {
            val properties = Properties()
            val inputStream = context.assets.open(LOCAL_PROPERTIES_FILENAME)
            properties.load(inputStream)
            val key = properties.getProperty(COINGECKO_API_KEY)
            if (!key.isNullOrEmpty()) {
                Log.d(TAG, "Found API key in assets")
                return key
            }
        } catch (e: IOException) {
            Log.d(TAG, "API key not found in assets: ${e.message}")
        }
        
        return ""
    }
} 