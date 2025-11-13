package com.example.psymed.ui.patient

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.analytics.PatientAnalyticsScreen
import com.example.psymed.ui.appointments.PatientAppointmentsScreen
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.appointments.SessionsUiState
import com.example.psymed.ui.auth.AuthUiState
import com.example.psymed.ui.health.HealthViewModel
import com.example.psymed.ui.health.PatientHealthScreen
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.medication.PatientMedicationScreen
import com.example.psymed.ui.tasks.PatientTasksScreen
import com.example.psymed.ui.tasks.TasksViewModel
import com.example.psymed.ui.theme.PsyMedColors
import java.time.LocalDate

private enum class PatientTab(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Appointments("Appointments", Icons.Outlined.CalendarToday),
    Health("Health", Icons.Outlined.FavoriteBorder),
    Medication("Medication", Icons.Outlined.Medication),
    Tasks("My Tasks", Icons.Outlined.TaskAlt),
    Analytics("Analytics", Icons.Outlined.BarChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    authState: AuthUiState,
    sessionsViewModel: SessionsViewModel,
    healthViewModel: HealthViewModel,
    medicationsViewModel: MedicationsViewModel,
    tasksViewModel: TasksViewModel,
    analyticsViewModel: AnalyticsViewModel,
    onNavigateToProfile: () -> Unit
) {
    val patientProfile = authState.patientProfile
    val patientId = patientProfile?.id ?: return PatientProfileMissing()

    var selectedTab by rememberSaveable { mutableStateOf(PatientTab.Appointments) }

    val sessionsState by sessionsViewModel.uiState.collectAsStateWithLifecycle()
    val healthState by healthViewModel.uiState.collectAsStateWithLifecycle()
    val medicationsState by medicationsViewModel.uiState.collectAsStateWithLifecycle()
    val tasksState by tasksViewModel.uiState.collectAsStateWithLifecycle()
    val analyticsState by analyticsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(patientId) {
        sessionsViewModel.loadPatientSessions(patientId)
        healthViewModel.loadPatientReports(patientId)
        medicationsViewModel.loadMedications(patientId)
        tasksViewModel.loadTasksByPatient(patientId)
        val now = LocalDate.now()
        analyticsViewModel.loadAnalytics(
            patientId = patientId,
            year = now.year.toString(),
            month = now.monthValue.toString()
        )
    }

    val topBarTitle = when (selectedTab) {
        PatientTab.Appointments -> "Appointments"
        PatientTab.Health -> "Health"
        PatientTab.Medication -> "Medication"
        PatientTab.Tasks -> "My Tasks"
        PatientTab.Analytics -> "Analytics Dashboard"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = PsyMedColors.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Profile",
                            tint = PsyMedColors.Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PsyMedColors.CardBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                tonalElevation = 6.dp,
                containerColor = PsyMedColors.CardBackground
            ) {
                PatientTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            PatientTab.Appointments -> PatientAppointmentsScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = sessionsState,
                onRefresh = { sessionsViewModel.loadPatientSessions(patientId) }
            )

            PatientTab.Health -> PatientHealthScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = healthState,
                onSaveReport = { mood, hunger, hydration, sleep, energy, callback ->
                    healthViewModel.saveDailyReport(
                        patientId = patientId,
                        mood = mood,
                        hunger = hunger,
                        hydration = hydration,
                        sleep = sleep,
                        energy = energy,
                        onResult = callback
                    )
                },
                onRefresh = { healthViewModel.loadPatientReports(patientId) }
            )

            PatientTab.Medication -> PatientMedicationScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = medicationsState,
                onRefresh = { medicationsViewModel.loadMedications(patientId) }
            )

            PatientTab.Tasks -> PatientTasksScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = tasksState,
                onToggleStatus = { task ->
                    tasksViewModel.toggleTaskStatus(task.sessionId, task) { _, _ -> }
                },
                onRefresh = { tasksViewModel.loadTasksByPatient(patientId) }
            )

            PatientTab.Analytics -> PatientAnalyticsScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = analyticsState,
                onRefresh = {
                    val now = LocalDate.now()
                    analyticsViewModel.loadAnalytics(
                        patientId = patientId,
                        year = now.year.toString(),
                        month = now.monthValue.toString()
                    )
                }
            )
        }
    }
}

@Composable
private fun PatientProfileMissing() {
    Scaffold { padding ->
        androidx.compose.material3.Text(
            text = "Unable to load patient profile.",
            modifier = Modifier
                .padding(padding)
                .padding(24.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

