package com.mockcrypto.data.remote

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    private const val TAG = "ApiServiceFactory"
    
    /**
     * Creates a CoinGeckoApiService instance with the API key from ApiKeyConfig
     */
    fun createCoinGeckoApiService(context: Context): CoinGeckoApiService {
        Log.d(TAG, "Creating CoinGeckoApiService")
        val apiKey = ApiKeyConfig.getCoinGeckoApiKey(context)
        Log.d(TAG, "Using API key: ${if (apiKey.isNotEmpty()) "API key obtained" else "Empty API key"}")
        
        // Create OkHttpClient with API key header interceptor
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(createApiKeyInterceptor(apiKey))
            .addInterceptor(createLoggingInterceptor())
            .build()
        
        // Create and return Retrofit service
        return Retrofit.Builder()
            .baseUrl(CoinGeckoApiService.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApiService::class.java)
    }
    
    private fun createApiKeyInterceptor(apiKey: String): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("x-cg-demo-api-key", apiKey)
            
            val request = requestBuilder.build()
            Log.d(TAG, "Adding API key header: x-cg-demo-api-key=${if (apiKey.isNotEmpty()) "API key added" else "Empty"}")
            chain.proceed(request)
        }
    }
    
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
} 