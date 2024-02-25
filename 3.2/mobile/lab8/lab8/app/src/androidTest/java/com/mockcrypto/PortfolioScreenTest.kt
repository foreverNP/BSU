package com.mockcrypto

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.ui.screens.portfolio.PortfolioScreen
import com.mockcrypto.ui.screens.portfolio.PortfolioUiState
import com.mockcrypto.ui.screens.portfolio.PortfolioViewModel
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class FakePortfolioViewModel(
    isLoading: Boolean = false,
    error: String? = null,
    accountState: DemoAccountState? = null,
    portfolioItems: List<PortfolioItem> = emptyList(),
    transactions: List<com.mockcrypto.domain.model.Transaction> = emptyList(),
    operationSuccess: Boolean? = null,
    operationMessage: String? = null
) : PortfolioViewModel() {
    init {
        _uiState.value = PortfolioUiState(
            isLoading = isLoading,
            error = error,
            accountState = accountState,
            portfolioItems = portfolioItems,
            transactions = transactions,
            operationSuccess = operationSuccess,
            operationMessage = operationMessage
        )
    }
    override fun loadPortfolioData() { /* ничего не делаем */ }
}

class PortfolioScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun portfolioScreen_showsAccountSummaryCard() {
        composeTestRule.setContent {
            PortfolioScreen(
                onCryptoClick = {},
                viewModel = FakePortfolioViewModel(
                    isLoading = false,
                    accountState = DemoAccountState(balance = BigDecimal("1000.00"))
                )
            )
        }
        // Замените на актуальный текст, если он отличается (например, "Available cash")
        composeTestRule.onNodeWithText("Доступные средства", substring = true).assertIsDisplayed()
    }

    @Test
    fun portfolioScreen_showsPortfolioItemCard() {
        val item = PortfolioItem(
            cryptoId = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            amount = BigDecimal("1.0"),
            averageBuyPrice = BigDecimal("10000.00")
        )
        composeTestRule.setContent {
            PortfolioScreen(
                onCryptoClick = {},
                viewModel = FakePortfolioViewModel(
                    isLoading = false,
                    accountState = DemoAccountState(
                        balance = BigDecimal("1000.00"),
                        portfolioItems = listOf(item)
                    ),
                    portfolioItems = listOf(item)
                )
            )
        }
        composeTestRule.onNodeWithTag("PortfolioItemCard_btc").assertIsDisplayed()
    }
} 