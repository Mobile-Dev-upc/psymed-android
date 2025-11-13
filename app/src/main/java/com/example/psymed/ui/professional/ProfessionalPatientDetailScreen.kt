package com.example.psymed.ui.professional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.psymed.domain.model.Task
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.appointments.PatientAppointmentsScreen
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.medication.PatientMedicationScreen
import com.example.psymed.ui.tasks.PatientTasksScreen
import com.example.psymed.ui.tasks.TasksViewModel
import com.example.psymed.ui.theme.PsyMedColors

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

    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = PsyMedColors.Primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info"
                        )
                    },
                    text = { Text("Info") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Medication,
                            contentDescription = "Medications"
                        )
                    },
                    text = { Text("Medications") }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Sessions"
                        )
                    },
                    text = { Text("Sessions") }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.TaskAlt,
                            contentDescription = "Tasks"
                        )
                    },
                    text = { Text("Tasks") }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> InfoTab(
                        patient = patient,
                        sessionsCount = sessionsState.sessions.size,
                        medicationsCount = medicationsState.medications.size,
                        tasksCount = tasksState.tasks.size
                    )
                    1 -> MedicationsTab(
                        uiState = medicationsState,
                        onRefresh = { medicationsViewModel.loadMedications(patientId) }
                    )
                    2 -> SessionsTab(
                        uiState = sessionsState,
                        onRefresh = { sessionsViewModel.loadPatientSessions(patientId) }
                    )
                    3 -> TasksTab(
                        uiState = tasksState,
                        onToggleStatus = { task ->
                            tasksViewModel.toggleTaskStatus(task.sessionId, task) { _, _ -> }
                        },
                        onRefresh = { tasksViewModel.loadTasksByPatient(patientId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoTab(
    patient: com.example.psymed.domain.model.PatientProfile?,
    sessionsCount: Int,
    medicationsCount: Int,
    tasksCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            patient?.let {
                InfoCard(
                    title = "Personal Information",
                    icon = Icons.Outlined.Info
                ) {
                    InfoRow(label = "Full Name", value = it.fullName)
                    InfoRow(label = "Email", value = it.email)
                    InfoRow(label = "Address", value = it.streetAddress)
                    InfoRow(label = "Patient ID", value = it.id.toString())
                }
            }
        }

        item {
            StatisticsCard(
                medicationsCount = medicationsCount,
                sessionsCount = sessionsCount,
                tasksCount = tasksCount
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PsyMedColors.Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PsyMedColors.Primary
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.width(120.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PsyMedColors.TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PsyMedColors.Primary
            )
        )
    }
}

@Composable
private fun StatisticsCard(
    medicationsCount: Int,
    sessionsCount: Int,
    tasksCount: Int
) {
    InfoCard(
        title = "Statistics",
        icon = Icons.Outlined.Info
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Medications",
                value = medicationsCount.toString(),
                icon = Icons.Outlined.Medication
            )
            StatItem(
                label = "Sessions",
                value = sessionsCount.toString(),
                icon = Icons.Outlined.CalendarToday
            )
            StatItem(
                label = "Tasks",
                value = tasksCount.toString(),
                icon = Icons.Outlined.TaskAlt
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PsyMedColors.Primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PsyMedColors.Primary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PsyMedColors.TextSecondary
            )
        )
    }
}

@Composable
private fun MedicationsTab(
    uiState: com.example.psymed.ui.medication.MedicationsUiState,
    onRefresh: () -> Unit
) {
    PatientMedicationScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = onRefresh
    )
}

@Composable
private fun SessionsTab(
    uiState: com.example.psymed.ui.appointments.SessionsUiState,
    onRefresh: () -> Unit
) {
    PatientAppointmentsScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = onRefresh
    )
}

@Composable
private fun TasksTab(
    uiState: com.example.psymed.ui.tasks.TasksUiState,
    onToggleStatus: (Task) -> Unit,
    onRefresh: () -> Unit
) {
    PatientTasksScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onToggleStatus = onToggleStatus,
        onRefresh = onRefresh
    )
}
