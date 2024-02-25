package com.mockcrypto.data.remote.mapper

import com.mockcrypto.data.remote.model.CryptoMarketData
import com.mockcrypto.domain.model.CryptoCurrency
import java.math.BigDecimal

object CryptoMapper {

    /**
     * Maps CryptoMarketData from the API to domain CryptoCurrency model
     */
    fun mapToDomain(marketData: CryptoMarketData): CryptoCurrency {
        return CryptoCurrency(
            id = marketData.id,
            symbol = marketData.symbol.uppercase(),
            name = marketData.name,
            price = marketData.currentPrice ?: BigDecimal.ZERO,
            changePercent24h = marketData.priceChangePercentage24h ?: 0.0,
            volume24h = marketData.totalVolume ?: BigDecimal.ZERO,
            iconUrl = marketData.image,
            sparklineData = marketData.sparklineIn7d?.price,
            marketCap = marketData.marketCap,
            circulatingSupply = formatSupply(marketData.circulatingSupply),
            maxSupply = formatSupply(marketData.maxSupply),
            allTimeHigh = marketData.ath
        )
    }

    /**
     * Format supply values to readable format with appropriate suffix (M, B, T)
     */
    private fun formatSupply(supplyValue: BigDecimal?): String? {
        if (supplyValue == null) return null
        
        return when {
            supplyValue >= BigDecimal(1_000_000_000_000) -> {
                val trillions = supplyValue.divide(BigDecimal(1_000_000_000_000), 2, BigDecimal.ROUND_HALF_UP)
                "$trillions T"
            }
            supplyValue >= BigDecimal(1_000_000_000) -> {
                val billions = supplyValue.divide(BigDecimal(1_000_000_000), 2, BigDecimal.ROUND_HALF_UP)
                "$billions B"
            }
            supplyValue >= BigDecimal(1_000_000) -> {
                val millions = supplyValue.divide(BigDecimal(1_000_000), 2, BigDecimal.ROUND_HALF_UP)
                "$millions M"
            }
            else -> supplyValue.setScale(0, BigDecimal.ROUND_HALF_UP).toString()
        }
    }

    /**
     * Maps a list of CryptoMarketData to a list of domain CryptoCurrency models
     */
    fun mapToDomain(marketDataList: List<CryptoMarketData>): List<CryptoCurrency> {
        return marketDataList.map { mapToDomain(it) }
    }
} 