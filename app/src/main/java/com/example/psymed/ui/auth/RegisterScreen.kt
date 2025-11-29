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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextDecoration
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
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }

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
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
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

            TermsAndConditions(
                accepted = acceptedTerms,
                onAcceptedChange = { acceptedTerms = it },
                isProfessional = true
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                ).all { it.isNotBlank() } && acceptedTerms,
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

@Composable
private fun TermsAndConditions(
    accepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit,
    isProfessional: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PsyMedColors.PrimaryLightest,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Terms and Conditions",
            style = MaterialTheme.typography.titleMedium.copy(
                color = PsyMedColors.Primary,
                fontWeight = FontWeight.Bold
            )
        )
        
        Text(
            text = "By creating an account, you agree to the following:",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PsyMedColors.TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TermItem(
                text = "Data Collection: The application collects and stores personal information including your name, email address, and physical address."
            )
            TermItem(
                text = "Health Data Tracking: ${if (isProfessional) "For patients you register, the app tracks" else "The app tracks"} mood states (emotional well-being on a 1-5 scale) and biological functions including hunger, hydration levels, sleep quality, and energy levels."
            )
            TermItem(
                text = "Data Storage: All collected data is stored securely on our servers to provide you with health analytics and insights."
            )
            TermItem(
                text = "No Advertising: This application does not display advertisements or sponsored content."
            )
            TermItem(
                text = "Data Privacy: We do not sell, share, or distribute your personal information or health data to third parties."
            )
            if (isProfessional) {
                TermItem(
                    text = "Patient Management: As a professional, you will have access to manage and view your registered patients' health data for treatment purposes."
                )
            }
            TermItem(
                text = "Purpose: Your data is used solely to provide health monitoring services, generate analytics, and support your healthcare journey."
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAcceptedChange(!accepted) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = accepted,
                onCheckedChange = onAcceptedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = PsyMedColors.Primary,
                    uncheckedColor = PsyMedColors.TextSecondary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "I accept the terms and conditions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PsyMedColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TermItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PsyMedColors.Primary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PsyMedColors.TextSecondary
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

