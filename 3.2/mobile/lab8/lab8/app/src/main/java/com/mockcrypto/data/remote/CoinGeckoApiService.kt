package com.mockcrypto.data.remote

import com.mockcrypto.data.remote.model.CryptoMarketData
import com.mockcrypto.data.remote.model.HistoricalDataResponse
import com.mockcrypto.data.remote.model.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApiService {

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }

    /**
     * Fetches market data for cryptocurrencies. Can be used for top N lists or specific coins.
     * https://docs.coingecko.com/v3.0.1/reference/coins-markets
     */
    @GET("coins/markets")
    suspend fun getCoinMarkets(
        @Query("vs_currency") vsCurrency: String, // e.g., "usd", "eur"
        @Query("ids") ids: String? = null, // Comma-separated coin ids (e.g., "bitcoin,ethereum")
        @Query("names") names: String? = null, // Comma-separated coin names (e.g., "bitcoin,ethereum")
        @Query("symbols") symbols: String? = null, // Comma-separated coin symbols (e.g., "btc,eth")
        @Query("include_tokens") includeTokens: String? = null, // "top" or "all". For symbols lookups, specify all to include all matching tokens. Default top returns top-ranked tokens (by market cap or volume)
        @Query("order") order: String? = "market_cap_desc", // e.g., "market_cap_desc", "volume_desc"
        @Query("per_page") perPage: Int? = 100, // 1-250
        @Query("page") page: Int? = 1,
        @Query("sparkline") sparkline: Boolean? = false, // Include 7-day sparkline data
        @Query("price_change_percentage") priceChangePercentage: String? = "24h", // e.g., "1h,24h,7d"
        @Query("locale") locale: String? = "en", // e.g., "en", "de", "es"
        @Query("precision") precision: String? = null // Number of decimal places for price, null for default
        // Note: 'names' and 'symbols' lookup params exist but have lower priority than 'ids' and 'category'
    ): Response<List<CryptoMarketData>>

    /**
     * Fetches historical market data for a specific coin within a date range.
     * https://docs.coingecko.com/v3.0.1/reference/coins-id-market-chart-range
     */
    @GET("coins/{id}/market_chart/range")
    suspend fun getCoinMarketChartRange(
        @Path("id") coinId: String, // e.g., "bitcoin"
        @Query("vs_currency") vsCurrency: String,
        @Query("from") fromTimestamp: Long, // UNIX timestamp (seconds)
        @Query("to") toTimestamp: Long,     // UNIX timestamp (seconds)
        @Query("precision") precision: String? = null // Number of decimal places for price
    ): Response<HistoricalDataResponse>

    /**
     * Searches for coins, categories, exchanges, and NFTs.
     * https://docs.coingecko.com/v3.0.1/reference/search-data
     */
    @GET("search")
    suspend fun search(
        @Query("query") query: String
    ): Response<SearchResult>
}