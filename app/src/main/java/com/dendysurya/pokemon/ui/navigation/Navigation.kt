package com.dendysurya.pokemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dendysurya.pokemon.ui.page.HomePage
import com.dendysurya.pokemon.ui.page.auth.LoginPage
import com.dendysurya.pokemon.viewmodel.MainViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.dendysurya.pokemon.ui.navigation.Routes.RegisterPage
import com.dendysurya.pokemon.ui.page.auth.RegisterPage

// Define route constants
object Routes {
    const val LoginPage = "login"
    const val RegisterPage = "register"
    const val HomePage = "home"
}

@Composable
fun Navigation(
    mainViewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    // Add initial=false parameter to collectAsState
    val isAuthenticated by mainViewModel.isAuthenticated.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (!isAuthenticated) Routes.LoginPage else Routes.HomePage,
    ) {
        composable(Routes.LoginPage) {
            LoginPage(viewModel = mainViewModel, navController = navController)
        }

        composable(Routes.RegisterPage) {
            RegisterPage(viewModel = mainViewModel, navController = navController)
        }

        composable(Routes.HomePage) {
            HomePage(viewModel = mainViewModel, navController = navController)
        }
    }
}