package com.mockcrypto.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mockcrypto.di.ServiceLocator
import com.mockcrypto.data.repository.CryptoRepository
import com.mockcrypto.domain.model.CryptoCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val cryptoList: List<CryptoCurrency> = emptyList(),
    val error: String? = null
)

class DashboardViewModel : ViewModel() {
    private val repository: CryptoRepository = ServiceLocator.provideCryptoRepository()
    
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadCryptoList()
    }
    
    fun loadCryptoList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getCryptoList()
                .onSuccess { cryptos ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        cryptoList = cryptos,
                        error = null
                    ) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message
                    ) }
                }
        }
    }
    
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 