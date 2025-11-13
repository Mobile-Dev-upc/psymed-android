package com.example.psymed.ui.professional

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.analytics.PatientAnalyticsScreen
import com.example.psymed.ui.appointments.PatientAppointmentsScreen
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.health.HealthViewModel
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.medication.PatientMedicationScreen
import com.example.psymed.ui.tasks.PatientTasksScreen
import com.example.psymed.ui.tasks.TasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalPatientDetailScreen(
    patientId: Int,
    viewModel: ProfessionalPatientsViewModel,
    sessionsViewModel: SessionsViewModel,
    medicationsViewModel: MedicationsViewModel,
    tasksViewModel: TasksViewModel,
    analyticsViewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val detailState by viewModel.uiState.collectAsStateWithLifecycle()
    val patient = detailState.selectedPatient

    LaunchedEffect(patientId) {
        viewModel.loadPatientDetails(patientId) { _, _ -> }
        sessionsViewModel.loadPatientSessions(patientId)
        medicationsViewModel.loadMedications(patientId)
        tasksViewModel.loadTasksByPatient(patientId)
        val now = java.time.LocalDate.now()
        analyticsViewModel.loadAnalytics(patientId, now.year.toString(), now.monthValue.toString())
    }

    val sessionsState by sessionsViewModel.uiState.collectAsStateWithLifecycle()
    val medicationsState by medicationsViewModel.uiState.collectAsStateWithLifecycle()
    val tasksState by tasksViewModel.uiState.collectAsStateWithLifecycle()
    val analyticsState by analyticsViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(patient?.fullName ?: "Patient Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                patient?.let {
                    Column {
                        Text(
                            text = it.fullName,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it.email,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it.streetAddress,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                SectionTitle("Appointments")
                PatientAppointmentsScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    uiState = sessionsState,
                    onRefresh = { sessionsViewModel.loadPatientSessions(patientId) }
                )
            }
            item {
                SectionTitle("Medications")
                PatientMedicationScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    uiState = medicationsState,
                    onRefresh = { medicationsViewModel.loadMedications(patientId) }
                )
            }
            item {
                SectionTitle("Tasks")
                PatientTasksScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    uiState = tasksState,
                    onToggleStatus = { task ->
                        tasksViewModel.toggleTaskStatus(task.sessionId, task) { _, _ -> }
                    },
                    onRefresh = { tasksViewModel.loadTasksByPatient(patientId) }
                )
            }
            item {
                SectionTitle("Analytics")
                PatientAnalyticsScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    uiState = analyticsState,
                    onRefresh = {
                        val now = java.time.LocalDate.now()
                        analyticsViewModel.loadAnalytics(patientId, now.year.toString(), now.monthValue.toString())
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
}

