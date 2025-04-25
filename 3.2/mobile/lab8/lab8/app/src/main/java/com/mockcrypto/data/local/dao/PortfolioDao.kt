package com.mockcrypto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mockcrypto.data.local.entity.AccountInfoEntity
import com.mockcrypto.data.local.entity.PortfolioItemEntity
import com.mockcrypto.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    // Portfolio Items operations
    @Query("SELECT * FROM portfolio_items")
    suspend fun getAllPortfolioItems(): List<PortfolioItemEntity>
    
    @Query("SELECT * FROM portfolio_items WHERE cryptoId = :cryptoId")
    suspend fun getPortfolioItemById(cryptoId: String): PortfolioItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioItem(item: PortfolioItemEntity)
    
    @Query("DELETE FROM portfolio_items WHERE cryptoId = :cryptoId")
    suspend fun deletePortfolioItem(cryptoId: String)
    
    // Transaction operations
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
    
    // Account info operations
    @Query("SELECT * FROM account_info WHERE id = 1")
    suspend fun getAccountInfo(): AccountInfoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountInfo(accountInfo: AccountInfoEntity)
    
    @Query("UPDATE account_info SET balance = :balance WHERE id = 1")
    suspend fun updateBalance(balance: String)
    
    // Reset operations
    @Query("DELETE FROM portfolio_items")
    suspend fun clearPortfolioItems()
    
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
    
    @Transaction
    suspend fun resetPortfolio(initialBalance: String) {
        clearPortfolioItems()
        clearTransactions()
        
        val accountInfo = getAccountInfo()
        if (accountInfo == null) {
            insertAccountInfo(AccountInfoEntity(balance = initialBalance))
        } else {
            updateBalance(initialBalance)
        }
    }
} 