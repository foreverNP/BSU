package com.mockcrypto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mockcrypto.ui.screens.dashboard.DashboardScreen
import com.mockcrypto.ui.screens.details.CryptoDetailScreen
import com.mockcrypto.ui.screens.portfolio.PortfolioScreen
import com.mockcrypto.ui.screens.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Dashboard.route,
        modifier = modifier
    ) {
        composable(route = AppDestination.Dashboard.route) {
            DashboardScreen(
                onCryptoClick = { cryptoId ->
                    navController.navigate(AppDestination.CryptoDetail.createRoute(cryptoId))
                }
            )
        }
        
        composable(route = AppDestination.Portfolio.route) {
            PortfolioScreen(
                onCryptoClick = { cryptoId ->
                    navController.navigate(AppDestination.CryptoDetail.createRoute(cryptoId))
                }
            )
        }
        
        composable(route = AppDestination.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = AppDestination.CryptoDetail.route,
            arguments = listOf(
                navArgument("cryptoId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""
            CryptoDetailScreen(
                cryptoId = cryptoId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
} 