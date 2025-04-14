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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.SnackbarDuration
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
import kotlinx.coroutines.delay

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

    // Track validation errors to display them inline
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Add separate interaction flags for each field
    var usernameInteracted by remember { mutableStateOf(false) }
    var emailInteracted by remember { mutableStateOf(false) }
    var passwordInteracted by remember { mutableStateOf(false) }
    var confirmPasswordInteracted by remember { mutableStateOf(false) }

    // Validate fields on change, but only show errors if the field has been interacted with
    LaunchedEffect(registerState) {
        // Username validation
        usernameError = if (usernameInteracted && registerState.username.isBlank()) {
            "Username is required"
        } else {
            null
        }

        // Email validation
        emailError = if (emailInteracted) {
            when {
                registerState.email.isBlank() -> "Email is required"
                !isValidEmail(registerState.email) -> "Please enter a valid email"
                else -> null
            }
        } else {
            null
        }

        // Password validation
        passwordError = if (passwordInteracted) {
            when {
                registerState.password.isBlank() -> "Password is required"
                registerState.password.length < 6 -> "Password must be at least 6 characters"
                else -> null
            }
        } else {
            null
        }

        // Confirm password validation
        confirmPasswordError = if (confirmPasswordInteracted) {
            when {
                registerState.confirmPassword.isBlank() -> "Please confirm your password"
                registerState.confirmPassword != registerState.password -> "Passwords don't match"
                else -> null
            }
        } else {
            null
        }
    }

    // Reset the form when navigating to this screen
    LaunchedEffect(Unit) {
        viewModel.resetRegisterForm()
        // Reset interaction flags
        usernameInteracted = false
        emailInteracted = false
        passwordInteracted = false
        confirmPasswordInteracted = false
    }

    // Show error message in snackbar if registration fails
    LaunchedEffect(registerState.errorMessage) {
        registerState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    var countdown by remember { mutableStateOf(3) }

    LaunchedEffect(registerState.isSuccess) {
        if (registerState.isSuccess) {


            snackbarHostState.showSnackbar("Account created successfully! Redirecting...")

                delay(1500)

            navController.navigate(Routes.LoginPage) {
                popUpTo(Routes.RegisterPage) { inclusive = true }
            }
        }
    }

// In your UI somewhere:
    if (registerState.isSuccess) {
        Text(text = "Redirecting in $countdown...")
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") }
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
                        onValueChange = {
                            usernameInteracted = true
                            viewModel.updateRegisterForm(username = it)
                        },
                        label = { Text("Username") },
                        singleLine = true,
                        isError = usernameError != null,
                        supportingText = {
                            if (usernameInteracted) {
                                usernameError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Field
                    OutlinedTextField(
                        value = registerState.email,
                        onValueChange = {
                            emailInteracted = true
                            viewModel.updateRegisterForm(email = it)
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = {
                            if (emailInteracted) {
                                emailError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Field
                    OutlinedTextField(
                        value = registerState.password,
                        onValueChange = {
                            passwordInteracted = true
                            viewModel.updateRegisterForm(password = it)
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordInteracted) {
                                passwordError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } ?: Text("Password must be at least 6 characters")
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password Field
                    OutlinedTextField(
                        value = registerState.confirmPassword,
                        onValueChange = {
                            confirmPasswordInteracted = true
                            viewModel.updateRegisterForm(confirmPassword = it)
                        },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        isError = confirmPasswordError != null,
                        supportingText = {
                            if (confirmPasswordInteracted) {
                                confirmPasswordError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()

                                // Set all interaction flags to true to validate all fields
                                usernameInteracted = true
                                emailInteracted = true
                                passwordInteracted = true
                                confirmPasswordInteracted = true

                                if (isFormValid(registerState)) {
                                    viewModel.register()
                                }
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register Button
                    Button(
                        onClick = {
                            // Set all interaction flags to true to validate all fields
                            usernameInteracted = true
                            emailInteracted = true
                            passwordInteracted = true
                            confirmPasswordInteracted = true

                            if (isFormValid(registerState)) {
                                viewModel.register()
                            }
                        },
                        enabled = !registerState.isLoading,
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

// Email validation helper function
private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
