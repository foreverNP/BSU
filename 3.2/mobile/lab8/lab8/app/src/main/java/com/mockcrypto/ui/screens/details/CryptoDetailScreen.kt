package com.mockcrypto.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mockcrypto.R
import com.mockcrypto.domain.model.TransactionType
import com.mockcrypto.ui.components.SparklineChart
import com.mockcrypto.ui.screens.portfolio.PortfolioViewModel
import java.math.BigDecimal
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoDetailScreen(
    cryptoId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    detailViewModel: CryptoDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CryptoDetailViewModel.Factory()
    ),
    portfolioViewModel: PortfolioViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = PortfolioViewModel.Factory()
    )
) {
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val portfolioUiState by portfolioViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load crypto details
    LaunchedEffect(cryptoId) {
        detailViewModel.loadCryptoDetails(cryptoId)
    }
    
    // Show operation result if there's any
    LaunchedEffect(portfolioUiState.operationSuccess, portfolioUiState.operationMessage) {
        val message = portfolioUiState.operationMessage
        if (portfolioUiState.operationSuccess != null && message != null) {
            snackbarHostState.showSnackbar(message = message)
            portfolioViewModel.resetOperationStatus()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = detailUiState.cryptoDetails?.name ?: stringResource(R.string.crypto_detail_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                detailUiState.isLoading -> {
                    LoadingState(modifier = Modifier.align(Alignment.Center))
                }
                
                detailUiState.error != null -> {
                    ErrorState(
                        message = detailUiState.error ?: stringResource(R.string.error_loading_data),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                detailUiState.cryptoDetails != null -> {
                    val crypto = detailUiState.cryptoDetails!!
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Price Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = crypto.symbol,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Text(
                                        text = "$${crypto.price.setScale(2, BigDecimal.ROUND_HALF_UP)}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Price chart
                                if (crypto.sparklineData != null && crypto.sparklineData.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 12.dp)
                                    ) {
                                        SparklineChart(
                                            sparklineData = crypto.sparklineData,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.chart_placeholder),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Trading Card
                        TradingCard(
                            cryptoId = crypto.id,
                            symbol = crypto.symbol,
                            name = crypto.name,
                            currentPrice = crypto.price,
                            availableBalance = portfolioUiState.accountState?.balance ?: BigDecimal.ZERO,
                            isLoading = portfolioUiState.isLoading,
                            onTradeExecuted = { type, amount, price ->
                                portfolioViewModel.executeTrade(
                                    cryptoId = crypto.id,
                                    symbol = crypto.symbol,
                                    name = crypto.name,
                                    amount = amount,
                                    price = price,
                                    type = type
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Market Info Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.market_info),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))

                                crypto.marketCap?.let { 
                                    MarketInfoRow(
                                        label = stringResource(R.string.market_cap),
                                        value = "$$it"
                                    )
                                }
                                
                                crypto.volume24h?.let { 
                                    MarketInfoRow(
                                        label = stringResource(R.string.volume_24h),
                                        value = "$$it"
                                    )
                                }
                                
                                crypto.circulatingSupply?.let { 
                                    MarketInfoRow(
                                        label = stringResource(R.string.circulating_supply),
                                        value = "$it ${crypto.symbol}"
                                    )
                                }
                                
                                crypto.allTimeHigh?.let { 
                                    MarketInfoRow(
                                        label = stringResource(R.string.all_time_high),
                                        value = "$$it"
                                    )
                                }

                                MarketInfoRow(
                                    label = stringResource(R.string.percent_change_24h),
                                    value = "${crypto.changePercent24h}%"
                                )
                                
                                crypto.maxSupply?.let {
                                    MarketInfoRow(
                                        label = stringResource(R.string.max_supply),
                                        value = "$it ${crypto.symbol}"
                                    )
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    Text(
                        text = stringResource(R.string.no_data_available),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.loading),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TradingCard(
    cryptoId: String,
    symbol: String,
    name: String,
    currentPrice: BigDecimal,
    availableBalance: BigDecimal,
    isLoading: Boolean,
    onTradeExecuted: (TransactionType, BigDecimal, BigDecimal) -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.BUY) }
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.trade),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${stringResource(R.string.available_balance)}: $${availableBalance.setScale(2, BigDecimal.ROUND_HALF_UP)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Type selector (Buy/Sell)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedType = TransactionType.BUY },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == TransactionType.BUY)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedType == TransactionType.BUY)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = stringResource(R.string.buy))
                }
                
                Button(
                    onClick = { selectedType = TransactionType.SELL },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == TransactionType.SELL)
                            Color(0xFFF44336)
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedType == TransactionType.SELL)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = stringResource(R.string.sell))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Amount input
            OutlinedTextField(
                value = amount,
                onValueChange = { 
                    // Only allow valid decimal numbers
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        amount = it
                    }
                },
                label = { Text(text = stringResource(R.string.amount_in_crypto, symbol)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total calculation
            val amountValue = amount.toDoubleOrNull()?.toBigDecimal() ?: BigDecimal.ZERO
            val totalValue = amountValue * currentPrice
            
            Text(
                text = "${stringResource(R.string.total)}: $${totalValue.setScale(2, BigDecimal.ROUND_HALF_UP)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Execute button
            Button(
                onClick = {
                    if (amountValue > BigDecimal.ZERO) {
                        onTradeExecuted(selectedType, amountValue, currentPrice)
                        amount = ""
                    }
                },
                enabled = !isLoading && amount.isNotEmpty() && amountValue > BigDecimal.ZERO,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.BUY)
                        MaterialTheme.colorScheme.primary
                    else
                        Color(0xFFF44336)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = if (selectedType == TransactionType.BUY)
                        stringResource(R.string.execute_buy, symbol)
                    else
                        stringResource(R.string.execute_sell, symbol)
                )
            }
        }
    }
}

@Composable
private fun MarketInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
} 