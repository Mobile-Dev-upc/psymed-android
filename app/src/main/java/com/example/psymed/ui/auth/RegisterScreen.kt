package com.example.psymed.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.psymed.domain.model.ProfessionalProfileRequest
import com.example.psymed.ui.components.PsyMedPrimaryButton
import com.example.psymed.ui.components.PsyMedTextButton
import com.example.psymed.ui.components.PsyMedTextField
import com.example.psymed.ui.theme.PsyMedColors
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (ProfessionalProfileRequest, (Boolean, String?) -> Unit) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var street by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(validationError) {
        validationError?.let { message ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
            validationError = null
        }
    }

    LaunchedEffect(uiState.error) {
        val error = uiState.error
        if (!error.isNullOrBlank()) {
            coroutineScope.launch { snackbarHostState.showSnackbar(error) }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Create Professional Account",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = PsyMedColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoBanner()
            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Personal Information")

            PsyMedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                leadingIcon = Icons.Outlined.PersonOutline,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PsyMedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                leadingIcon = Icons.Outlined.PersonOutline,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PsyMedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Outlined.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Address")

            PsyMedTextField(
                value = street,
                onValueChange = { street = it },
                label = "Street",
                leadingIcon = Icons.Outlined.Home,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PsyMedTextField(
                value = city,
                onValueChange = { city = it },
                label = "City",
                leadingIcon = Icons.Outlined.LocationCity,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PsyMedTextField(
                value = country,
                onValueChange = { country = it },
                label = "Country",
                leadingIcon = Icons.Outlined.Flag,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Account Credentials")

            PsyMedTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                leadingIcon = Icons.Outlined.Badge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PsyMedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PsyMedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            PsyMedPrimaryButton(
                text = "Register",
                loading = uiState.isLoading,
                enabled = listOf(
                    firstName,
                    lastName,
                    street,
                    city,
                    country,
                    email,
                    username,
                    password,
                    confirmPassword
                ).all { it.isNotBlank() },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (password != confirmPassword) {
                    validationError = "Passwords do not match"
                    return@PsyMedPrimaryButton
                }
                val request = ProfessionalProfileRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    street = street.trim(),
                    city = city.trim(),
                    country = country.trim(),
                    email = email.trim(),
                    username = username.trim(),
                    password = password
                )
                onRegister(request) { success, message ->
                    if (success) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Account created successfully. You can now sign in.")
                        }
                        onNavigateBack()
                    } else if (!message.isNullOrBlank()) {
                        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            PsyMedTextButton(
                text = "Already have an account? Sign in",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onNavigateBack
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            color = PsyMedColors.Primary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun InfoBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PsyMedColors.PrimaryLightest,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Professional Registration",
            style = MaterialTheme.typography.titleMedium.copy(
                color = PsyMedColors.Primary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Register as a health professional to manage your patients.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Patients are registered by their professional.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PsyMedColors.TextSecondary,
                fontStyle = FontStyle.Italic
            )
        )
    }
}

