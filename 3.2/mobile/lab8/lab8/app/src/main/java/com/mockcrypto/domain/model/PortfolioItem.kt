package com.mockcrypto.domain.model

import java.math.BigDecimal

data class PortfolioItem(
    val cryptoId: String,
    val symbol: String,
    val name: String,
    val amount: BigDecimal,
    val averageBuyPrice: BigDecimal,
    val iconUrl: String? = null,

    val currentPrice: BigDecimal? = null,
    val currentValue: BigDecimal? = null,
    val profitLoss: BigDecimal? = null,
    val profitLossPercentage: BigDecimal? = null
) {
    fun withCalculatedValues(
        currentPrice: BigDecimal,
        iconUrl: String? = this.iconUrl
    ): PortfolioItem {
        val currentValue = amount * currentPrice
        val costBasis = amount * averageBuyPrice
        val profitLoss = currentValue - costBasis
        val profitLossPercentage = if (costBasis > BigDecimal.ZERO) {
            profitLoss.divide(costBasis, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal("100"))
        } else {
            BigDecimal.ZERO
        }
        
        return copy(
            currentPrice = currentPrice,
            currentValue = currentValue,
            profitLoss = profitLoss,
            profitLossPercentage = profitLossPercentage,
            iconUrl = iconUrl ?: this.iconUrl
        )
    }
}