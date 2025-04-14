package com.dendysurya.pokemon.ui.page.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dendysurya.pokemon.ui.navigation.Routes
import com.dendysurya.pokemon.ui.theme.PokemonTheme
import com.dendysurya.pokemon.viewmodel.MainViewModel
import com.dendysurya.pokemon.viewmodel.RegisterUiState

// Email validation helper function
private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    viewModel: MainViewModel,
    navController: NavController
) {
    val registerState by viewModel.registerUiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Reset the form when navigating to this screen
    LaunchedEffect(Unit) {
        viewModel.resetRegisterForm()
    }

    // Show error message in snackbar if registration fails
    LaunchedEffect(registerState.errorMessage) {
        registerState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Navigate to login when registration succeeds
    LaunchedEffect(registerState.isSuccess) {
        if (registerState.isSuccess) {
            navController.navigate(Routes.LoginPage) {
                popUpTo(Routes.RegisterPage) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") }
                // Note: We're removing the navigationIcon temporarily
                // since we don't have the icons dependency
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Registration Header
                    Text(
                        text = "Join Pokémon App",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Create your account to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Username Field
                    OutlinedTextField(
                        value = registerState.username,
                        onValueChange = { viewModel.updateRegisterForm(username = it) },
                        label = { Text("Username") },
                        singleLine = true,
                        isError = registerState.errorMessage != null && registerState.username.isBlank(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    OutlinedTextField(
                        value = registerState.email,
                        onValueChange = { viewModel.updateRegisterForm(email = it) },
                        label = { Text("Email") },
                        singleLine = true,
                        isError = registerState.errorMessage != null &&
                                (registerState.email.isBlank() || !isValidEmail(registerState.email)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = registerState.password,
                        onValueChange = { viewModel.updateRegisterForm(password = it) },
                        label = { Text("Password") },
                        singleLine = true,
                        isError = registerState.errorMessage != null &&
                                (registerState.password.isBlank() || registerState.password.length < 6),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        // Using text instead of icon for password visibility
                        trailingIcon = {
                            TextButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = if (passwordVisible) "Hide" else "Show",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    OutlinedTextField(
                        value = registerState.confirmPassword,
                        onValueChange = { viewModel.updateRegisterForm(confirmPassword = it) },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        isError = registerState.errorMessage != null &&
                                (registerState.confirmPassword.isBlank() ||
                                        registerState.password != registerState.confirmPassword),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (isFormValid(registerState)) {
                                    viewModel.register()
                                }
                            }
                        ),
                        // Using text instead of icon for password visibility
                        trailingIcon = {
                            TextButton(
                                onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = if (confirmPasswordVisible) "Hide" else "Show",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register Button
                    Button(
                        onClick = { viewModel.register() },
                        enabled = !registerState.isLoading && isFormValid(registerState),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (registerState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Create Account")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login button
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Already have an account? Sign In")
                    }
                }
            }
        }
    }
}

// Helper function to validate the registration form
private fun isFormValid(state: RegisterUiState): Boolean {
    return state.username.isNotBlank() &&
            state.email.isNotBlank() && isValidEmail(state.email) &&
            state.password.isNotBlank() && state.password.length >= 6 &&
            state.confirmPassword == state.password
}


