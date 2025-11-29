package com.example.psymed.ui.professional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.psymed.domain.model.PatientProfileRequest
import com.example.psymed.ui.auth.AuthUiState
import com.example.psymed.ui.components.PsyMedPrimaryButton
import com.example.psymed.ui.components.PsyMedTextField
import com.example.psymed.ui.theme.PsyMedColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalAddPatientScreen(
    authState: AuthUiState,
    onCreatePatient: (PatientProfileRequest, (Boolean, String?) -> Unit) -> Unit,
    onClose: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val professionalId = authState.professionalProfile?.id ?: 0

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var street by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(authState.error) {
        authState.error?.let { message ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Patient") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PsyMedColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoBanner(professionalId = professionalId)

            PsyMedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                leadingIcon = Icons.Outlined.PersonOutline,
                modifier = Modifier.fillMaxWidth()
            )
            PsyMedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                leadingIcon = Icons.Outlined.PersonOutline,
                modifier = Modifier.fillMaxWidth()
            )
            PsyMedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Outlined.Email,
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle("Address")
            PsyMedTextField(
                value = street,
                onValueChange = { street = it },
                label = "Street",
                leadingIcon = Icons.Outlined.Home,
                modifier = Modifier.fillMaxWidth()
            )
            PsyMedTextField(
                value = city,
                onValueChange = { city = it },
                label = "City",
                leadingIcon = Icons.Outlined.LocationCity,
                modifier = Modifier.fillMaxWidth()
            )
            PsyMedTextField(
                value = country,
                onValueChange = { country = it },
                label = "Country",
                leadingIcon = Icons.Outlined.Flag,
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle("Account Credentials")
            PsyMedTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                leadingIcon = Icons.Outlined.PersonOutline,
                modifier = Modifier.fillMaxWidth()
            )
            PsyMedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
            PsyMedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TermsAndConditions(
                accepted = acceptedTerms,
                onAcceptedChange = { acceptedTerms = it },
                isProfessional = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            PsyMedPrimaryButton(
                text = "Create Patient Account",
                loading = authState.isLoading,
                enabled = listOf(
                    firstName, lastName, email, street, city, country, username, password, confirmPassword
                ).all { it.isNotBlank() } && acceptedTerms,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (password != confirmPassword) {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
                    return@PsyMedPrimaryButton
                }
                val request = PatientProfileRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    street = street.trim(),
                    city = city.trim(),
                    country = country.trim(),
                    email = email.trim(),
                    username = username.trim(),
                    password = password,
                    professionalId = professionalId
                )
                onCreatePatient(request) { success, message ->
                    coroutineScope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar("Patient created successfully")
                            onClose()
                        } else if (!message.isNullOrBlank()) {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            color = PsyMedColors.Primary,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun InfoBanner(professionalId: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PsyMedColors.PrimaryLightest, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "You are creating an account for your patient.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
        )
        Text(
            text = "Professional ID: $professionalId",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PsyMedColors.Primary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Share the username and password with your patient after registration.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PsyMedColors.TextSecondary,
                textAlign = TextAlign.Left
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
            .background(PsyMedColors.PrimaryLightest, RoundedCornerShape(12.dp))
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
                text = "Health Data Tracking: The app tracks mood states (emotional well-being on a 1-5 scale) and biological functions including hunger, hydration levels, sleep quality, and energy levels."
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
            TermItem(
                text = "Professional Access: Your registered healthcare professional will have access to your health data to provide treatment and monitor your progress."
            )
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

