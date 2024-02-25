package com.mockcrypto.ui.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mockcrypto.di.ServiceLocator
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import com.mockcrypto.domain.usecase.PortfolioUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val accountState: DemoAccountState? = null,
    val portfolioItems: List<PortfolioItem> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null,
    val operationSuccess: Boolean? = null, // Null means no operation, true means success, false means failure
    val operationMessage: String? = null
)

open class PortfolioViewModel : ViewModel() {
    
    private val portfolioUseCases: PortfolioUseCases = ServiceLocator.providePortfolioUseCases()
    
    val _uiState = MutableStateFlow(PortfolioUiState(isLoading = true))
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    init {
        loadPortfolioData()
    }
    
    open fun loadPortfolioData() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true, 
                error = null,
                operationSuccess = null,
                operationMessage = null
            ) }
            
            try {
                // Получаем состояние аккаунта (включая портфель) через UseCase
                val accountStateResult = portfolioUseCases.getAccountState()
                val transactionsResult = portfolioUseCases.getTransactionHistory()
                
                // Проверяем результаты
                if (accountStateResult.isFailure) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = accountStateResult.exceptionOrNull()?.message ?: "Failed to load account data"
                    ) }
                    return@launch
                }
                
                if (transactionsResult.isFailure) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = transactionsResult.exceptionOrNull()?.message ?: "Failed to load transactions"
                    ) }
                    return@launch
                }
                
                // Получаем данные
                val accountState = accountStateResult.getOrThrow()
                val transactions = transactionsResult.getOrThrow()
                
                // Обновляем UI состояние
                _uiState.update { it.copy(
                    isLoading = false,
                    accountState = accountState,
                    portfolioItems = accountState.portfolioItems,
                    transactions = transactions,
                    error = null
                ) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    ) 
                }
            }
        }
    }
    
    fun executeTrade(
        cryptoId: String,
        symbol: String,
        name: String,
        amount: BigDecimal,
        price: BigDecimal,
        type: TransactionType
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, operationSuccess = null, operationMessage = null) }
            
            try {
                val result = portfolioUseCases.executeTrade(
                    cryptoId = cryptoId,
                    symbol = symbol,
                    name = name,
                    amount = amount,
                    price = price,
                    type = type
                )
                
                if (result.isSuccess) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        operationSuccess = true,
                        operationMessage = if (type == TransactionType.BUY) 
                            "Successfully purchased $amount $symbol" 
                        else 
                            "Successfully sold $amount $symbol"
                    ) }
                    loadPortfolioData()
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        operationSuccess = false,
                        operationMessage = result.exceptionOrNull()?.message ?: "Operation failed"
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    operationSuccess = false,
                    operationMessage = e.message ?: "An unexpected error occurred"
                ) }
            }
        }
    }
    
    fun resetPortfolio() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = portfolioUseCases.resetPortfolio()
                if (result.isSuccess) {
                    _uiState.update { it.copy(
                        operationSuccess = true,
                        operationMessage = "Portfolio has been reset to default state"
                    ) }
                    loadPortfolioData()
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        operationSuccess = false,
                        operationMessage = result.exceptionOrNull()?.message ?: "Failed to reset portfolio"
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    operationSuccess = false,
                    operationMessage = e.message ?: "An unexpected error occurred"
                ) }
            }
        }
    }
    
    fun resetOperationStatus() {
        _uiState.update { it.copy(operationSuccess = null, operationMessage = null) }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
                return PortfolioViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 