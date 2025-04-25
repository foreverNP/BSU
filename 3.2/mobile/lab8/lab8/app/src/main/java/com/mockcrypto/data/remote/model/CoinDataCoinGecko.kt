package com.mockcrypto.data.remote.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

// --- Model for coins/markets endpoint ---
data class CryptoMarketData(
    @SerializedName("id") val id: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?, // URL for the icon
    @SerializedName("current_price") val currentPrice: BigDecimal?,
    @SerializedName("market_cap") val marketCap: BigDecimal?,
    @SerializedName("market_cap_rank") val marketCapRank: Int?,
    @SerializedName("fully_diluted_valuation") val fullyDilutedValuation: BigDecimal?,
    @SerializedName("total_volume") val totalVolume: BigDecimal?,
    @SerializedName("high_24h") val high24h: BigDecimal?,
    @SerializedName("low_24h") val low24h: BigDecimal?,
    @SerializedName("price_change_24h") val priceChange24h: BigDecimal?,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage24h: Double?,
    @SerializedName("market_cap_change_24h") val marketCapChange24h: BigDecimal?,
    @SerializedName("market_cap_change_percentage_24h") val marketCapChangePercentage24h: Double?,
    @SerializedName("circulating_supply") val circulatingSupply: BigDecimal?,
    @SerializedName("total_supply") val totalSupply: BigDecimal?,
    @SerializedName("max_supply") val maxSupply: BigDecimal?,
    @SerializedName("ath") val ath: BigDecimal?,
    @SerializedName("ath_change_percentage") val athChangePercentage: Double?,
    @SerializedName("ath_date") val athDate: String?, // ISO8601 date string
    @SerializedName("atl") val atl: BigDecimal?,
    @SerializedName("atl_change_percentage") val atlChangePercentage: Double?,
    @SerializedName("atl_date") val atlDate: String?, // ISO8601 date string
    @SerializedName("last_updated") val lastUpdated: String?, // ISO8601 date string
    // Optional: Price change percentage for other timeframes if requested
    @SerializedName("price_change_percentage_1h_in_currency") val priceChangePercentage1h: Double?,
    @SerializedName("price_change_percentage_7d_in_currency") val priceChangePercentage7d: Double?,
    @SerializedName("price_change_percentage_14d_in_currency") val priceChangePercentage14d: Double?,
    @SerializedName("price_change_percentage_30d_in_currency") val priceChangePercentage30d: Double?,
    @SerializedName("price_change_percentage_200d_in_currency") val priceChangePercentage200d: Double?,
    @SerializedName("price_change_percentage_1y_in_currency") val priceChangePercentage1y: Double?,
    @SerializedName("sparkline_in_7d") val sparklineIn7d: SparklineData?
)

data class SparklineData(
    @SerializedName("price") val price: List<BigDecimal>?
)

// --- Model for coins/{id}/market_chart/range endpoint ---
data class HistoricalDataResponse(
    @SerializedName("prices") val prices: List<List<BigDecimal>>, // List of [timestamp, price]
    @SerializedName("market_caps") val marketCaps: List<List<BigDecimal>>, // List of [timestamp, market_cap]
    @SerializedName("total_volumes") val totalVolumes: List<List<BigDecimal>> // List of [timestamp, total_volume]
)

// --- Models for /search endpoint ---
data class SearchResult(
    @SerializedName("coins") val coins: List<SearchCoin>,
//    @SerializedName("exchanges") val exchanges: List<SearchExchange>,
//    @SerializedName("icos") val icos: List<Any>, // Define if needed, API shows empty array
//    @SerializedName("categories") val categories: List<SearchCategory>,
//    @SerializedName("nfts") val nfts: List<SearchNft>
)

data class SearchCoin(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("api_symbol") val apiSymbol: String, // coin api symbol
    @SerializedName("symbol") val symbol: String,
    @SerializedName("market_cap_rank") val marketCapRank: Int?,
    @SerializedName("thumb") val thumb: String?, // coin thumb image url
    @SerializedName("large") val large: String? // coin large image url
)

//data class SearchExchange(
//     @SerializedName("id") val id: String,
//     @SerializedName("name") val name: String,
//     @SerializedName("market_type") val marketType: String?,
//     @SerializedName("thumb") val thumb: String?,
//     @SerializedName("large") val large: String?
//)
//
//data class SearchCategory(
//    @SerializedName("id") val id: Int, // Note: API shows integer ID here
//    @SerializedName("name") val name: String?
//)
//
//data class SearchNft(
//    @SerializedName("id") val id: String,
//    @SerializedName("name") val name: String?,
//    @SerializedName("symbol") val symbol: String?,
//    @SerializedName("thumb") val thumb: String?
//)