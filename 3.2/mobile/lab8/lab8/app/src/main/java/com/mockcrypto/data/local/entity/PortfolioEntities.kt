package com.mockcrypto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.mockcrypto.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "portfolio_items")
data class PortfolioItemEntity(
    @PrimaryKey
    val cryptoId: String,
    val symbol: String,
    val name: String,
    val amount: String, // BigDecimal stored as String
    val averageBuyPrice: String, // BigDecimal stored as String
    val iconUrl: String?
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val cryptoId: String,
    val symbol: String,
    val type: String, // TransactionType as String
    val amount: String, // BigDecimal stored as String
    val pricePerUnit: String, // BigDecimal stored as String
    val timestamp: Long // LocalDateTime as epoch milliseconds
)

@Entity(tableName = "account_info")
data class AccountInfoEntity(
    @PrimaryKey
    val id: Int = 1, // Single row table
    val balance: String // BigDecimal stored as String
)

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): Long =
        value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long): LocalDateTime =
        Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
} 