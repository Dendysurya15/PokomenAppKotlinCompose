package com.dendysurya.pokemon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dendysurya.pokemon.data.local.database.AppDatabase
import com.dendysurya.pokemon.data.network.NetworkModule
import com.dendysurya.pokemon.data.repository.AuthRepository
import com.dendysurya.pokemon.ui.navigation.Navigation
import com.dendysurya.pokemon.ui.theme.PokemonTheme
import com.dendysurya.pokemon.viewmodel.MainViewModel

@Composable
fun PokemonApp() {
    val context = LocalContext.current

    // Database setup
    val database = AppDatabase.getDatabase(context)
    val userDao = database.userDao()
    val authRepository = remember { AuthRepository(context, userDao) }

    // API Service setup
    val retrofit = remember { NetworkModule.provideRetrofit() }
    val apiService = remember { NetworkModule.provideApiService(retrofit) }
    val pokemonRepository = remember { NetworkModule.providePokemonRepository(apiService) }

    // Create MainViewModel with repositories
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(authRepository, pokemonRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    val mainViewModel: MainViewModel = viewModel(factory = factory)

    PokemonTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Navigation(mainViewModel = mainViewModel)
        }
    }
}