package com.mockcrypto.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mockcrypto.data.local.dao.PortfolioDao
import com.mockcrypto.data.local.entity.AccountInfoEntity
import com.mockcrypto.data.local.entity.Converters
import com.mockcrypto.data.local.entity.PortfolioItemEntity
import com.mockcrypto.data.local.entity.TransactionEntity

@Database(
    entities = [
        PortfolioItemEntity::class,
        TransactionEntity::class,
        AccountInfoEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun portfolioDao(): PortfolioDao
    
    companion object {
        private const val DATABASE_NAME = "mock_crypto_db"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 