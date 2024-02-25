package com.mockcrypto.domain.model

import java.math.BigDecimal

data class DemoAccountState(
    val balance: BigDecimal, // Available cash
    val totalPortfolioValue: BigDecimal = BigDecimal.ZERO, // Current value of all assets
    val totalProfitLoss: BigDecimal = BigDecimal.ZERO, // Total profit/loss

    val portfolioCost: BigDecimal = BigDecimal.ZERO, // Total cost basis
    val profitLossPercentage: BigDecimal = BigDecimal.ZERO, // Percentage P/L
    val portfolioItems: List<PortfolioItem> = emptyList() // Portfolio items with calculated values
) {
    val totalBalance: BigDecimal
        get() = balance + totalPortfolioValue
}