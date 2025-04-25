package com.mockcrypto.data.repository

import android.content.Context
import com.mockcrypto.data.remote.mapper.CryptoMapper
import com.mockcrypto.data.remote.ApiServiceFactory
import com.mockcrypto.data.remote.CoinGeckoApiService
import com.mockcrypto.domain.model.CryptoCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface CryptoRepository {
    suspend fun getCryptoList(): Result<List<CryptoCurrency>>
    suspend fun getCryptoDetails(id: String): Result<CryptoCurrency>
    suspend fun getCryptoDetailsMap(ids: List<String>): Result<Map<String, CryptoCurrency>>
}

class CoinGeckoCryptoRepository(
    private val context: Context
) : CryptoRepository {

    private val apiService: CoinGeckoApiService = ApiServiceFactory.createCoinGeckoApiService(context)

    companion object {
        private const val DEFAULT_CURRENCY = "usd"
        private const val DEFAULT_ORDER = "market_cap_desc"
        private const val DEFAULT_PER_PAGE = 250
        private const val DEFAULT_PRICE_CHANGE = "24h"
    }

    override suspend fun getCryptoList(): Result<List<CryptoCurrency>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCoinMarkets(
                vsCurrency = DEFAULT_CURRENCY,
                order = DEFAULT_ORDER,
                perPage = DEFAULT_PER_PAGE,
                priceChangePercentage = DEFAULT_PRICE_CHANGE,
                sparkline = true
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(CryptoMapper.mapToDomain(body))
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("API call failed with code: ${response.code()}, message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // id - comma separated list of ids
    override suspend fun getCryptoDetails(id: String): Result<CryptoCurrency> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCoinMarkets(
                vsCurrency = DEFAULT_CURRENCY,
                ids = id,
                perPage = 1,
                priceChangePercentage = DEFAULT_PRICE_CHANGE,
                sparkline = true
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isNotEmpty()) {
                    Result.success(CryptoMapper.mapToDomain(body.first()))
                } else {
                    Result.failure(NoSuchElementException("Crypto with id $id not found"))
                }
            } else {
                Result.failure(IOException("API call failed with code: ${response.code()}, message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCryptoDetailsMap(ids: List<String>): Result<Map<String, CryptoCurrency>> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) {
            return@withContext Result.success(emptyMap())
        }
        try {
            val response = apiService.getCoinMarkets(
                vsCurrency = DEFAULT_CURRENCY,
                ids = ids.joinToString(","),
                perPage = ids.size,
                priceChangePercentage = DEFAULT_PRICE_CHANGE,
                sparkline = true
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isNotEmpty()) {
                    val cryptoList = CryptoMapper.mapToDomain(body) 
                    val detailsMap = cryptoList.associateBy { it.id }
                    Result.success(detailsMap)
                } else {
                    Result.failure(NoSuchElementException("Crypto details not found for IDs: ${ids.joinToString(",")}"))
                }
            } else {
                Result.failure(IOException("API call failed with code: ${response.code()}, message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}