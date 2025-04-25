package com.mockcrypto

import com.mockcrypto.data.repository.CryptoRepository
import com.mockcrypto.data.repository.PortfolioRepository
import com.mockcrypto.domain.model.CryptoCurrency
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.usecase.PortfolioUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import org.mockito.Mockito.spy
import org.mockito.Mockito.doReturn

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioUseCasesTest {
    private lateinit var portfolioRepository: PortfolioRepository
    private lateinit var cryptoRepository: CryptoRepository
    private lateinit var useCases: PortfolioUseCases

    @Before
    fun setUp() {
        portfolioRepository = mock(PortfolioRepository::class.java)
        cryptoRepository = mock(CryptoRepository::class.java)
        useCases = spy(PortfolioUseCases(portfolioRepository, cryptoRepository))
    }

    @Test
    fun `getPortfolioWithPrices returns portfolio with updated prices`() = runTest {
        val portfolioItems = listOf(
            PortfolioItem(
                cryptoId = "btc",
                symbol = "BTC",
                name = "Bitcoin",
                amount = BigDecimal("2.0"),
                averageBuyPrice = BigDecimal("10000.00")
            )
        )
        val cryptoDetails = mapOf(
            "btc" to CryptoCurrency(
                id = "btc",
                symbol = "BTC",
                name = "Bitcoin",
                price = BigDecimal("20000.00"),
                changePercent24h = 5.0,
                volume24h = BigDecimal("1000000.00")
            )
        )
        `when`(portfolioRepository.getPortfolioItems()).thenReturn(Result.success(portfolioItems))
        `when`(cryptoRepository.getCryptoDetailsMap(listOf("btc"))).thenReturn(Result.success(cryptoDetails))

        val result = useCases.getPortfolioWithPrices()
        assertTrue(result.isSuccess)
        val updated = result.getOrThrow()
        assertEquals(1, updated.size)
        assertTrue(updated[0].currentPrice!!.compareTo(BigDecimal("20000.00")) == 0)
        assertTrue(updated[0].currentValue!!.compareTo(BigDecimal("40000.00")) == 0)
    }

    @Test
    fun `getAccountState returns correct totals`() = runTest {
        val portfolioItems = listOf(
            PortfolioItem(
                cryptoId = "btc",
                symbol = "BTC",
                name = "Bitcoin",
                amount = BigDecimal("1.0"),
                averageBuyPrice = BigDecimal("10000.00"),
                currentPrice = BigDecimal("15000.00"),
                currentValue = BigDecimal("15000.00"),
                profitLoss = BigDecimal("5000.00"),
                profitLossPercentage = BigDecimal("50.00")
            )
        )
        val accountState = DemoAccountState(balance = BigDecimal("5000.00"))
        `when`(portfolioRepository.getDemoAccountState()).thenReturn(Result.success(accountState))
        // Мокаем getPortfolioWithPrices через сам use case, чтобы не дублировать логику
        doReturn(Result.success(portfolioItems)).`when`(useCases).getPortfolioWithPrices()

        // Для этого теста мы просто проверим, что getAccountState корректно агрегирует значения
        val result = useCases.getAccountState()
        assertTrue(result.isSuccess)
        val state = result.getOrThrow()
        assertTrue(state.balance.compareTo(BigDecimal("5000.00")) == 0)
        assertTrue(state.totalPortfolioValue.compareTo(BigDecimal("15000.00")) == 0)
        assertTrue(state.totalProfitLoss.compareTo(BigDecimal("5000.00")) == 0)
        assertTrue(state.portfolioCost.compareTo(BigDecimal("10000.00")) == 0)
        assertTrue(state.profitLossPercentage.compareTo(BigDecimal("50.0000")) == 0)
        assertEquals(1, state.portfolioItems.size)
    }

    @Test
    fun `executeTrade proxies result from repository`() = runTest {
        val expectedResult = Result.success(Unit)
        `when`(portfolioRepository.simulateTrade(
            cryptoId = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            amount = BigDecimal("1.0"),
            price = BigDecimal("10000.00"),
            type = com.mockcrypto.domain.model.TransactionType.BUY
        )).thenReturn(expectedResult)

        val result = useCases.executeTrade(
            cryptoId = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            amount = BigDecimal("1.0"),
            price = BigDecimal("10000.00"),
            type = com.mockcrypto.domain.model.TransactionType.BUY
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `getTransactionHistory proxies result from repository`() = runTest {
        val transactions = listOf<com.mockcrypto.domain.model.Transaction>()
        val expectedResult = Result.success(transactions)
        `when`(portfolioRepository.getTransactionHistory()).thenReturn(expectedResult)

        val result = useCases.getTransactionHistory()
        assertEquals(expectedResult, result)
    }
} 