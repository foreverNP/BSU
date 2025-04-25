package com.mockcrypto.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    object Dashboard : AppDestination(
        route = "dashboard",
        titleResId = com.mockcrypto.R.string.bottom_nav_dashboard,
        icon = Icons.Default.Dashboard
    )
    
    object Portfolio : AppDestination(
        route = "portfolio",
        titleResId = com.mockcrypto.R.string.bottom_nav_portfolio,
        icon = Icons.Default.Wallet
    )
    
    object Profile : AppDestination(
        route = "profile",
        titleResId = com.mockcrypto.R.string.bottom_nav_profile,
        icon = Icons.Default.Person
    )
    
    object CryptoDetail : AppDestination(
        route = "crypto_detail/{cryptoId}",
        titleResId = com.mockcrypto.R.string.crypto_detail_title,
        icon = Icons.Default.Dashboard
    ) {
        fun createRoute(cryptoId: String) = "crypto_detail/$cryptoId"
    }
} 