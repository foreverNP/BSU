package com.mockcrypto.di

import android.content.Context
import com.mockcrypto.data.repository.CoinGeckoCryptoRepository
import com.mockcrypto.data.repository.CryptoRepository
import com.mockcrypto.data.repository.MockSettingsRepository
import com.mockcrypto.data.repository.MockUserRepository
import com.mockcrypto.data.repository.PortfolioRepository
import com.mockcrypto.data.repository.RoomPortfolioRepository
import com.mockcrypto.data.repository.SettingsRepository
import com.mockcrypto.data.repository.UserRepository
import com.mockcrypto.domain.usecase.PortfolioUseCases
import com.mockcrypto.domain.usecase.ProfileUseCases

/**
 * Simple service locator for dependency injection
 */
object ServiceLocator {
    private var applicationContext: Context? = null
    
    private var cryptoRepository: CryptoRepository? = null
    private var portfolioRepository: PortfolioRepository? = null
    private var userRepository: UserRepository? = null
    private var settingsRepository: SettingsRepository? = null
    
    private var portfolioUseCases: PortfolioUseCases? = null
    private var profileUseCases: ProfileUseCases? = null
    
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        
        createCryptoRepository()
        createPortfolioRepository()
        createUserRepository()
        createSettingsRepository()
        
        createPortfolioUseCases()
        createProfileUseCases()
    }
    
    fun provideCryptoRepository(): CryptoRepository {
        return cryptoRepository ?: createCryptoRepository()
    }
    
    fun providePortfolioRepository(): PortfolioRepository {
        return portfolioRepository ?: createPortfolioRepository()
    }
    
    fun provideUserRepository(): UserRepository {
        return userRepository ?: createUserRepository()
    }
    
    fun provideSettingsRepository(): SettingsRepository {
        return settingsRepository ?: createSettingsRepository()
    }
    
    fun providePortfolioUseCases(): PortfolioUseCases {
        return portfolioUseCases ?: createPortfolioUseCases()
    }
    
    fun provideProfileUseCases(): ProfileUseCases {
        return profileUseCases ?: createProfileUseCases()
    }
    
    private fun createCryptoRepository(): CryptoRepository {
        val context = checkNotNull(applicationContext) { "ServiceLocator not initialized" }
        val repo = CoinGeckoCryptoRepository(context)
        cryptoRepository = repo
        return repo
    }
    
    private fun createPortfolioRepository(): PortfolioRepository {
        val context = checkNotNull(applicationContext) { "ServiceLocator not initialized" }
        val repo = RoomPortfolioRepository(context)
        portfolioRepository = repo
        return repo
    }
    
    private fun createUserRepository(): UserRepository {
        val repo = MockUserRepository()
        userRepository = repo
        return repo
    }
    
    private fun createSettingsRepository(): SettingsRepository {
        val repo = MockSettingsRepository()
        settingsRepository = repo
        return repo
    }
    
    private fun createPortfolioUseCases(): PortfolioUseCases {
        val useCases = PortfolioUseCases(
            portfolioRepository = providePortfolioRepository(),
            cryptoRepository = provideCryptoRepository()
        )
        portfolioUseCases = useCases
        return useCases
    }
    
    private fun createProfileUseCases(): ProfileUseCases {
        val useCases = ProfileUseCases(
            provideUserRepository(),
            provideSettingsRepository(),
            providePortfolioRepository()
        )
        profileUseCases = useCases
        return useCases
    }
} 