package com.dendysurya.pokemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendysurya.pokemon.data.repository.AuthRepository
import com.dendysurya.pokemon.data.repository.PokemonRepository
import com.dendysurya.pokemon.model.PokemonBasic
import com.dendysurya.pokemon.model.PokemonDetailState
import com.dendysurya.pokemon.model.PokemonListState
import com.dendysurya.pokemon.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

/**
 * MainViewModel - Manages app authentication state and operations
 */
class MainViewModel(private val authRepository: AuthRepository,private val pokemonRepository: PokemonRepository) : ViewModel() {

    // Login State
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    // Register State
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    // Authentication state from repository
    val isAuthenticated = authRepository.isAuthenticated

    // Update login form fields
    fun updateLoginForm(username: String? = null, password: String? = null) {
        _loginUiState.update {
            it.copy(
                username = username ?: it.username,
                password = password ?: it.password,
                errorMessage = null
            )
        }
    }

    // Handle login
    fun login() {
        val username = _loginUiState.value.username
        val password = _loginUiState.value.password

        if (username.isBlank() || password.isBlank()) {
            _loginUiState.update {
                it.copy(errorMessage = "Username and password cannot be empty")
            }
            return
        }

        _loginUiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(username, password)

            result.fold(
                onSuccess = { user ->
                    _loginUiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { exception ->
                    _loginUiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = exception.message ?: "Login failed"
                        )
                    }
                }
            )
        }
    }

    // Update register form fields
    fun updateRegisterForm(
        username: String? = null,
        email: String? = null,
        password: String? = null,
        confirmPassword: String? = null
    ) {
        _registerUiState.update {
            it.copy(
                username = username ?: it.username,
                email = email ?: it.email,
                password = password ?: it.password,
                confirmPassword = confirmPassword ?: it.confirmPassword,
                errorMessage = null
            )
        }
    }

    // Reset register form
    fun resetRegisterForm() {
        _registerUiState.value = RegisterUiState()
    }

    // Handle registration
    fun register() {
        val state = _registerUiState.value

        // Basic validation
        if (state.username.isBlank()) {
            _registerUiState.update { it.copy(errorMessage = "Username cannot be empty") }
            return
        }
        if (state.email.isBlank() || !isValidEmail(state.email)) {
            _registerUiState.update { it.copy(errorMessage = "Valid email is required") }
            return
        }
        if (state.password.length < 6) {
            _registerUiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.password != state.confirmPassword) {
            _registerUiState.update { it.copy(errorMessage = "Passwords don't match") }
            return
        }

        _registerUiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.register(
                username = state.username,
                email = state.email,
                password = state.password
            )

            result.fold(
                onSuccess = { user ->
                    _registerUiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { exception ->
                    _registerUiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = exception.message ?: "Registration failed"
                        )
                    }
                }
            )
        }
    }

    // Logout function
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    // Helper function for email validation
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Add these to your MainViewModel class

    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    fun loadUserProfile() {
        _userProfileState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _userProfileState.update {
                    it.copy(
                        isLoading = false,
                        userData = user,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _userProfileState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    // Pokemon list state
    private val _pokemonListState = MutableStateFlow(PokemonListState())
    val pokemonListState: StateFlow<PokemonListState> = _pokemonListState.asStateFlow()

    // Pokemon detail state
    private val _pokemonDetailState = MutableStateFlow<PokemonDetailState?>(null)
    val pokemonDetailState: StateFlow<PokemonDetailState?> = _pokemonDetailState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // All loaded pokemon for local search
    private val _allLoadedPokemon = mutableListOf<PokemonBasic>()

    // Filtered pokemon list
    val filteredPokemonList = combine(_pokemonListState, _searchQuery) { state, query ->
        if (query.isBlank()) {
            state.pokemonList
        } else {
            _allLoadedPokemon.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Load initial pokemon list
    init {
        loadMorePokemon()
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Load more pokemon (pagination)
    fun loadMorePokemon() {
        if (_pokemonListState.value.isLoading || !_pokemonListState.value.canLoadMore) return

        _pokemonListState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val offset = _allLoadedPokemon.size
            val limit = 10

            val result = pokemonRepository.getPokemonList(offset, limit)

            result.fold(
                onSuccess = { response ->
                    _allLoadedPokemon.addAll(response.results)

                    _pokemonListState.update { state ->
                        state.copy(
                            isLoading = false,
                            pokemonList = if (_searchQuery.value.isBlank()) {
                                _allLoadedPokemon
                            } else {
                                state.pokemonList
                            },
                            canLoadMore = response.next != null,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _pokemonListState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load Pokemon"
                        )
                    }
                }
            )
        }
    }

    // Load pokemon detail
    fun loadPokemonDetail(nameOrId: String) {
        _pokemonDetailState.value = PokemonDetailState(isLoading = true)

        viewModelScope.launch {
            val result = pokemonRepository.getPokemonDetail(nameOrId)

            result.fold(
                onSuccess = { pokemon ->
                    _pokemonDetailState.value = PokemonDetailState(
                        isLoading = false,
                        pokemon = pokemon,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _pokemonDetailState.value = PokemonDetailState(
                        isLoading = false,
                        pokemon = null,
                        errorMessage = error.message ?: "Failed to load Pokemon details"
                    )
                }
            )
        }
    }

    // Clear detail state
    fun clearPokemonDetail() {
        _pokemonDetailState.value = null
    }


}

data class UserProfileState(
    val isLoading: Boolean = false,
    val userData: UserData? = null,
    val errorMessage: String? = null
)

// Login UI State
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

// Register UI State
data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)