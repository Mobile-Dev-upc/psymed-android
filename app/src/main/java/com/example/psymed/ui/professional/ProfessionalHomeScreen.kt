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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.psymed.ui.auth.AuthUiState
import com.example.psymed.ui.theme.PsyMedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalHomeScreen(
    authState: AuthUiState,
    viewModel: ProfessionalPatientsViewModel,
    onAddPatient: () -> Unit,
    onViewPatient: (Int) -> Unit,
    onEditPatient: (Int) -> Unit,
    onDeletePatient: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val professionalId = authState.professionalProfile?.id

    LaunchedEffect(professionalId) {
        professionalId?.let { viewModel.loadPatients(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Patients") },
                actions = {
                    if (professionalId != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Badge,
                                contentDescription = null,
                                tint = PsyMedColors.Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ID: $professionalId",
                                style = MaterialTheme.typography.bodySmall.copy(color = PsyMedColors.Primary)
                            )
                        }
                    }
                    IconButton(onClick = { professionalId?.let { viewModel.loadPatients(it) } }) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPatient,
                containerColor = PsyMedColors.Primary
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add patient", tint = Color.White)
            }
        },
        containerColor = PsyMedColors.Background
    ) { padding ->
        when {
            uiState.isLoading && uiState.patients.isEmpty() -> ProfessionalLoading(modifier = Modifier.padding(padding))
            uiState.error != null -> ProfessionalError(
                message = uiState.error!!,
                onRetry = { professionalId?.let { viewModel.loadPatients(it) } },
                modifier = Modifier.padding(padding)
            )
            uiState.patients.isEmpty() -> ProfessionalEmpty(modifier = Modifier.padding(padding))
            else -> ProfessionalPatientsList(
                uiState = uiState,
                onViewPatient = onViewPatient,
                onEditPatient = onEditPatient,
                onDeletePatient = onDeletePatient,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ProfessionalLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.CircularProgressIndicator(color = PsyMedColors.Primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading patients...", color = PsyMedColors.TextSecondary)
    }
}

@Composable
private fun ProfessionalError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading patients",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = PsyMedColors.Primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(
            onClick = onRetry,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
        ) {
            Text("Try again", color = Color.White)
        }
    }
}

@Composable
private fun ProfessionalEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No patients yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Share your professional ID with patients so they can register.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ProfessionalPatientsList(
    uiState: ProfessionalPatientsUiState,
    onViewPatient: (Int) -> Unit,
    onEditPatient: (Int) -> Unit,
    onDeletePatient: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.patients, key = { it.id }) { patient ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .clickable { onViewPatient(patient.id) },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = patient.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                InfoRow(icon = Icons.Outlined.Email, value = patient.email)
                InfoRow(icon = Icons.Outlined.LocationOn, value = patient.streetAddress)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onEditPatient(patient.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit patient",
                            tint = PsyMedColors.Primary
                        )
                    }
                    IconButton(onClick = { onDeletePatient(patient.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete patient",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PsyMedColors.Primary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
        )
    }
}

