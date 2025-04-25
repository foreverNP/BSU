package com.mockcrypto.data.repository

import android.content.Context
import com.mockcrypto.data.local.AppDatabase
import com.mockcrypto.data.local.entity.AccountInfoEntity
import com.mockcrypto.data.local.mapper.PortfolioMapper
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

interface PortfolioRepository {
    suspend fun getDemoAccountState(): Result<DemoAccountState>
    suspend fun getPortfolioItems(): Result<List<PortfolioItem>>
    suspend fun getTransactionHistory(): Result<List<Transaction>>
    suspend fun simulateTrade(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ): Result<Unit>
    
    suspend fun resetPortfolio(): Result<Unit>
}

class RoomPortfolioRepository(
    context: Context
) : PortfolioRepository {
    
    private val portfolioDao = AppDatabase.getInstance(context).portfolioDao()

    private val INITIAL_BALANCE = BigDecimal("1000000.00")
    
    override suspend fun getDemoAccountState(): Result<DemoAccountState> = withContext(Dispatchers.IO) {
        try {
            val accountInfo = portfolioDao.getAccountInfo()
            val balance = accountInfo?.let { BigDecimal(it.balance) } ?: initializeAccount()

            return@withContext Result.success(DemoAccountState(
                balance = balance,
                totalPortfolioValue = BigDecimal.ZERO,
                totalProfitLoss = BigDecimal.ZERO
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun initializeAccount(): BigDecimal {
        portfolioDao.insertAccountInfo(AccountInfoEntity(balance = INITIAL_BALANCE.toPlainString()))
        return INITIAL_BALANCE
    }
    
    override suspend fun getPortfolioItems(): Result<List<PortfolioItem>> = withContext(Dispatchers.IO) {
        try {
            val entities = portfolioDao.getAllPortfolioItems()
            val portfolioItems = entities.map { PortfolioMapper.mapToDomain(it) }
            Result.success(portfolioItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionHistory(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val entities = portfolioDao.getAllTransactions()
            val transactions = entities.map { PortfolioMapper.mapToDomain(it) }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun simulateTrade(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cost = amount * price
            val transactionId = UUID.randomUUID().toString()
            
            when (type) {
                TransactionType.BUY -> {
                    // Get current balance
                    val accountInfo = portfolioDao.getAccountInfo() 
                        ?: AccountInfoEntity(balance = INITIAL_BALANCE.toPlainString())
                    val currentBalance = BigDecimal(accountInfo.balance)
                    
                    // Check if balance is sufficient
                    if (currentBalance < cost) {
                        return@withContext Result.failure(Exception("Insufficient balance"))
                    }
                    
                    // Update balance
                    val newBalance = currentBalance - cost
                    portfolioDao.updateBalance(newBalance.toPlainString())
                    
                    // Update portfolio item
                    val existingItem = portfolioDao.getPortfolioItemById(cryptoId)
                    if (existingItem != null) {
                        // Calculate new average buy price
                        val currentAmount = BigDecimal(existingItem.amount)
                        val currentAvgPrice = BigDecimal(existingItem.averageBuyPrice)
                        
                        val newAmount = currentAmount + amount
                        val newAvgPrice = calculateNewAverageBuyPrice(
                            currentAmount, currentAvgPrice, amount, price
                        )
                        
                        val updatedItem = existingItem.copy(
                            amount = newAmount.toPlainString(),
                            averageBuyPrice = newAvgPrice.toPlainString()
                        )
                        portfolioDao.insertPortfolioItem(updatedItem)
                    } else {
                        // Create new portfolio item
                        val newItem = PortfolioItem(
                            cryptoId = cryptoId,
                            symbol = symbol,
                            name = name,
                            amount = amount,
                            averageBuyPrice = price,
                            iconUrl = null
                        )
                        portfolioDao.insertPortfolioItem(PortfolioMapper.mapToEntity(newItem))
                    }
                }
                
                TransactionType.SELL -> {
                    // Get portfolio item
                    val existingItem = portfolioDao.getPortfolioItemById(cryptoId)
                        ?: return@withContext Result.failure(Exception("Not enough $symbol to sell or item not found"))
                    
                    val currentAmount = BigDecimal(existingItem.amount)
                    if (currentAmount < amount) {
                        return@withContext Result.failure(Exception("Not enough $symbol to sell"))
                    }
                    
                    // Update balance
                    val accountInfo = portfolioDao.getAccountInfo() 
                        ?: AccountInfoEntity(balance = INITIAL_BALANCE.toPlainString())
                    val currentBalance = BigDecimal(accountInfo.balance)
                    val newBalance = currentBalance + cost
                    portfolioDao.updateBalance(newBalance.toPlainString())
                    
                    // Update portfolio item
                    val newAmount = currentAmount - amount
                    if (newAmount > BigDecimal.ZERO) {
                        val updatedItem = existingItem.copy(amount = newAmount.toPlainString())
                        portfolioDao.insertPortfolioItem(updatedItem)
                    } else {
                        portfolioDao.deletePortfolioItem(cryptoId)
                    }
                }
            }
            
            // Record transaction
            val transaction = Transaction(
                id = transactionId,
                cryptoId = cryptoId,
                symbol = symbol,
                type = type,
                amount = amount,
                pricePerUnit = price,
                timestamp = LocalDateTime.now()
            )
            portfolioDao.insertTransaction(PortfolioMapper.mapToEntity(transaction))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateNewAverageBuyPrice(
        currentAmount: BigDecimal,
        currentAvgPrice: BigDecimal,
        newAmount: BigDecimal,
        newPrice: BigDecimal
    ): BigDecimal {
        val totalValue = (currentAmount * currentAvgPrice) + (newAmount * newPrice)
        val totalAmount = currentAmount + newAmount
        return totalValue.divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP)
    }
    
    override suspend fun resetPortfolio(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            portfolioDao.resetPortfolio(INITIAL_BALANCE.toPlainString())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}