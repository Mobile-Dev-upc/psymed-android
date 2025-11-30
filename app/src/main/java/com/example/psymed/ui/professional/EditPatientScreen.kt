package com.example.psymed.ui.professional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.psymed.domain.model.UpdatePatientProfileRequest
import com.example.psymed.ui.components.PsyMedPrimaryButton
import com.example.psymed.ui.components.PsyMedTextField
import com.example.psymed.ui.theme.PsyMedColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatientScreen(
    patientId: Int,
    viewModel: ProfessionalPatientsViewModel,
    onUpdatePatient: (Int, UpdatePatientProfileRequest, (Boolean, String?) -> Unit) -> Unit,
    onClose: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Parse fullName into firstName and lastName
    val nameParts = uiState.selectedPatient?.fullName?.split(" ") ?: emptyList()
    val defaultFirstName = nameParts.firstOrNull() ?: ""
    val defaultLastName = nameParts.drop(1).joinToString(" ")

    var firstName by rememberSaveable { mutableStateOf(defaultFirstName) }
    var lastName by rememberSaveable { mutableStateOf(defaultLastName) }
    var email by rememberSaveable { mutableStateOf(uiState.selectedPatient?.email ?: "") }
    var street by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }

    // Load patient details if not already loaded
    LaunchedEffect(patientId) {
        if (uiState.selectedPatient == null || uiState.selectedPatient?.id != patientId) {
            viewModel.loadPatientDetails(patientId) { success, message ->
                if (!success) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message ?: "Failed to load patient details")
                    }
                } else {
                    // Update form fields when patient is loaded
                    val patient = uiState.selectedPatient
                    if (patient != null) {
                        val parts = patient.fullName.split(" ")
                        firstName = parts.firstOrNull() ?: ""
                        lastName = parts.drop(1).joinToString(" ")
                        email = patient.email
                        // Note: streetAddress might contain city and country, but we'll leave them empty
                        // as the Flutter version does
                    }
                }
            }
        } else {
            // Patient already loaded, update form fields
            val patient = uiState.selectedPatient
            if (patient != null) {
                val parts = patient.fullName.split(" ")
                firstName = parts.firstOrNull() ?: ""
                lastName = parts.drop(1).joinToString(" ")
                email = patient.email
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Patient") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
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
            // Info banner showing current patient info
            InfoBanner(
                patientName = uiState.selectedPatient?.fullName ?: "",
                currentAddress = uiState.selectedPatient?.streetAddress ?: ""
            )

            SectionTitle("Personal Information")
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

            Spacer(modifier = Modifier.height(12.dp))

            PsyMedPrimaryButton(
                text = "Update Patient",
                loading = uiState.isLoading,
                enabled = listOf(firstName, lastName, email, street, city, country).all { it.isNotBlank() },
                modifier = Modifier.fillMaxWidth()
            ) {
                val request = UpdatePatientProfileRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    street = street.trim(),
                    city = city.trim(),
                    country = country.trim(),
                    email = email.trim()
                )
                onUpdatePatient(patientId, request) { success, message ->
                    coroutineScope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar("Patient updated successfully")
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
private fun InfoBanner(patientName: String, currentAddress: String) {
    if (patientName.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    PsyMedColors.PrimaryLightest,
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Editing patient: $patientName",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PsyMedColors.TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            if (currentAddress.isNotEmpty()) {
                androidx.compose.material3.Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = PsyMedColors.Divider
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Current Address:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PsyMedColors.TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = currentAddress,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PsyMedColors.Primary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ Please re-enter the address information in the fields below",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PsyMedColors.Warning,
                        fontWeight = FontWeight.Medium
                    )
                )
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

