package com.mockcrypto.domain.usecase

import com.mockcrypto.data.repository.CryptoRepository
import com.mockcrypto.data.repository.PortfolioRepository
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioUseCases(
    private val portfolioRepository: PortfolioRepository,
    private val cryptoRepository: CryptoRepository
) {
    /**
     * Gets portfolio items with current market values
     */
    suspend fun getPortfolioWithPrices(): Result<List<PortfolioItem>> {
        try {
            // Get portfolio items
            val portfolioItemsResult = portfolioRepository.getPortfolioItems()
            if (portfolioItemsResult.isFailure) {
                return Result.failure(portfolioItemsResult.exceptionOrNull() 
                    ?: Exception("Failed to get portfolio items"))
            }
            
            val portfolioItems = portfolioItemsResult.getOrThrow()
            if (portfolioItems.isEmpty()) {
                return Result.success(emptyList())
            }

            // Extract unique crypto IDs from portfolio
            val cryptoIds = portfolioItems.map { it.cryptoId }.distinct()

            // Get current prices and details for all required cryptos in one call
            val cryptoDetailsMapResult = cryptoRepository.getCryptoDetailsMap(cryptoIds)

            val cryptoDetailsMap = if (cryptoDetailsMapResult.isSuccess) {
                cryptoDetailsMapResult.getOrThrow()
            } else {
                println("Failed to get crypto details: ${cryptoDetailsMapResult.exceptionOrNull()?.message}")
                emptyMap()
            }

            // Update portfolio items with fetched data
            val portfolioItemsWithPrices = portfolioItems.map { item ->
                val cryptoDetails = cryptoDetailsMap[item.cryptoId]
                item.withCalculatedValues(
                    // Use fetched price if available, otherwise fallback to average buy price
                    currentPrice = cryptoDetails?.price ?: item.averageBuyPrice, 
                    iconUrl = cryptoDetails?.iconUrl 
                )
            }
            
            return Result.success(portfolioItemsWithPrices)
        } catch (e: Exception) {
            // Catch potential exceptions from getOrThrow or other operations
            return Result.failure(e)
        }
    }
    
    /**
     * Gets the account state with calculated portfolio values
     */
    suspend fun getAccountState(): Result<DemoAccountState> {
        try {
            // Get base account state (balance)
            val accountStateResult = portfolioRepository.getDemoAccountState()
            if (accountStateResult.isFailure) {
                return accountStateResult
            }
            
            val baseAccountState = accountStateResult.getOrThrow()
            
            // Get portfolio with values
            val portfolioItemsResult = getPortfolioWithPrices()
            if (portfolioItemsResult.isFailure) {
                return Result.success(baseAccountState)
            }
            
            val portfolioItems = portfolioItemsResult.getOrThrow()
            
            // Calculate totals
            val totalValue = portfolioItems.sumOf { it.currentValue ?: BigDecimal.ZERO }
            val totalCost = portfolioItems.sumOf { it.amount * it.averageBuyPrice }
            val totalProfitLoss = portfolioItems.sumOf { it.profitLoss ?: BigDecimal.ZERO }
            val totalProfitLossPercentage = if (totalCost > BigDecimal.ZERO) {
                totalProfitLoss.divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal("100"))
            } else {
                BigDecimal.ZERO
            }
            
            // Return updated account state with portfolio values
            return Result.success(
                DemoAccountState(
                    balance = baseAccountState.balance,
                    totalPortfolioValue = totalValue,
                    totalProfitLoss = totalProfitLoss,
                    portfolioCost = totalCost,
                    profitLossPercentage = totalProfitLossPercentage,
                    portfolioItems = portfolioItems
                )
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    /**
     * Execute a trade (buy or sell)
     */
    suspend fun executeTrade(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ): Result<Unit> {
        return portfolioRepository.simulateTrade(
            cryptoId = cryptoId,
            symbol = symbol,
            name = name,
            amount = amount,
            price = price,
            type = type
        )
    }
    
    /**
     * Get transaction history
     */
    suspend fun getTransactionHistory(): Result<List<Transaction>> {
        return portfolioRepository.getTransactionHistory()
    }
    
    /**
     * Reset portfolio to initial state
     */
    suspend fun resetPortfolio(): Result<Unit> {
        return portfolioRepository.resetPortfolio()
    }
} 