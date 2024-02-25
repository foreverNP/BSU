package com.mockcrypto.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime // Используем java.time для дат/времени

enum class TransactionType {
    BUY, SELL
}

data class Transaction(
    val id: String,
    val cryptoId: String,
    val symbol: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val pricePerUnit: BigDecimal, // Цена за единицу в момент сделки
    val timestamp: LocalDateTime
) {
    val totalCost: BigDecimal
        get() = amount * pricePerUnit
}