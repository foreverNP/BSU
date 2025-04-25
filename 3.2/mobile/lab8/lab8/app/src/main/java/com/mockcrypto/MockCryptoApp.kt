package com.mockcrypto

import android.app.Application
import com.mockcrypto.di.ServiceLocator

class MockCryptoApp : Application() {
    override fun onCreate() {
        super.onCreate()        
        ServiceLocator.initialize(applicationContext)
    }
} 