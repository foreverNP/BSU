package com.mockcrypto.data.local.mapper

import com.mockcrypto.data.local.entity.PortfolioItemEntity
import com.mockcrypto.data.local.entity.TransactionEntity
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId

object PortfolioMapper {

    fun mapToDomain(entity: PortfolioItemEntity): PortfolioItem {
        return PortfolioItem(
            cryptoId = entity.cryptoId,
            symbol = entity.symbol,
            name = entity.name,
            amount = BigDecimal(entity.amount),
            averageBuyPrice = BigDecimal(entity.averageBuyPrice),
            iconUrl = entity.iconUrl
        )
    }

    fun mapToEntity(domain: PortfolioItem): PortfolioItemEntity {
        return PortfolioItemEntity(
            cryptoId = domain.cryptoId,
            symbol = domain.symbol,
            name = domain.name,
            amount = domain.amount.toPlainString(),
            averageBuyPrice = domain.averageBuyPrice.toPlainString(),
            iconUrl = domain.iconUrl
        )
    }

    fun mapToDomain(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            cryptoId = entity.cryptoId,
            symbol = entity.symbol,
            type = TransactionType.valueOf(entity.type),
            amount = BigDecimal(entity.amount),
            pricePerUnit = BigDecimal(entity.pricePerUnit),
            timestamp = Instant.ofEpochMilli(entity.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )
    }

    fun mapToEntity(domain: Transaction): TransactionEntity {
        return TransactionEntity(
            id = domain.id,
            cryptoId = domain.cryptoId,
            symbol = domain.symbol,
            type = domain.type.name,
            amount = domain.amount.toPlainString(),
            pricePerUnit = domain.pricePerUnit.toPlainString(),
            timestamp = domain.timestamp.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
    }
} 