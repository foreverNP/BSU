package com.mockcrypto

import com.mockcrypto.domain.model.PortfolioItem
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class PortfolioItemTest {
    @Test
    fun `withCalculatedValues calculates profit and percentage correctly`() {
        val item = PortfolioItem(
            cryptoId = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            amount = BigDecimal("2.0"),
            averageBuyPrice = BigDecimal("10000.00")
        )
        val updated = item.withCalculatedValues(currentPrice = BigDecimal("15000.00"))
        assertTrue(updated.currentValue!!.compareTo(BigDecimal("30000.00")) == 0)
        assertTrue(updated.profitLoss!!.compareTo(BigDecimal("10000.00")) == 0)
        assertTrue(updated.profitLossPercentage!!.compareTo(BigDecimal("50.0000")) == 0)
    }

    @Test
    fun `withCalculatedValues handles zero cost basis`() {
        val item = PortfolioItem(
            cryptoId = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            amount = BigDecimal.ZERO,
            averageBuyPrice = BigDecimal("10000.00")
        )
        val updated = item.withCalculatedValues(currentPrice = BigDecimal("15000.00"))
        assertTrue(updated.currentValue!!.compareTo(BigDecimal.ZERO) == 0)
        assertTrue(updated.profitLoss!!.compareTo(BigDecimal.ZERO) == 0)
        assertTrue(updated.profitLossPercentage!!.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `withCalculatedValues handles negative profit`() {
        val item = PortfolioItem(
            cryptoId = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            amount = BigDecimal("1.0"),
            averageBuyPrice = BigDecimal("20000.00")
        )
        val updated = item.withCalculatedValues(currentPrice = BigDecimal("15000.00"))
        assertTrue(updated.currentValue!!.compareTo(BigDecimal("15000.00")) == 0)
        assertTrue(updated.profitLoss!!.compareTo(BigDecimal("-5000.00")) == 0)
        assertTrue(updated.profitLossPercentage!!.compareTo(BigDecimal("-25.0000")) == 0)
    }
} 