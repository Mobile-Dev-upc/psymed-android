package com.example.psymed.ui.professional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.psymed.domain.model.Medication
import com.example.psymed.domain.model.MedicationRequest
import com.example.psymed.domain.model.MedicationUpdateRequest
import com.example.psymed.domain.model.Session
import com.example.psymed.domain.model.SessionCreateRequest
import com.example.psymed.domain.model.SessionUpdateRequest
import com.example.psymed.domain.model.Task
import com.example.psymed.domain.model.TaskRequest
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.appointments.PatientAppointmentsScreen
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.auth.AuthUiState
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.tasks.PatientTasksScreen
import com.example.psymed.ui.tasks.TasksViewModel
import com.example.psymed.ui.theme.PsyMedColors
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalPatientDetailScreen(
    patientId: Int,
    authState: AuthUiState,
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
        },
        bottomBar = {
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
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (selectedTabIndex) {
                0 -> InfoTab(
                    patient = patient,
                    sessionsCount = sessionsState.sessions.size,
                    medicationsCount = medicationsState.medications.size,
                    tasksCount = tasksState.tasks.size,
                    moodStates = analyticsState.moodStates,
                    biologicalFunctions = analyticsState.biologicalFunctions
                )
                1 -> MedicationsTab(
                    patientId = patientId,
                    uiState = medicationsState,
                    viewModel = medicationsViewModel,
                    onRefresh = { medicationsViewModel.loadMedications(patientId) }
                )
                2 -> SessionsTab(
                    patientId = patientId,
                    professionalId = authState.professionalProfile?.id,
                    uiState = sessionsState,
                    viewModel = sessionsViewModel,
                    onRefresh = { sessionsViewModel.loadPatientSessions(patientId) }
                )
                3 -> TasksTab(
                    patientId = patientId,
                    sessionsState = sessionsState,
                    uiState = tasksState,
                    viewModel = tasksViewModel,
                    onToggleStatus = { task ->
                        tasksViewModel.toggleTaskStatus(task.sessionId, task) { _, _ -> }
                    },
                    onRefresh = { tasksViewModel.loadTasksByPatient(patientId) }
                )
            }
        }
    }
}

@Composable
private fun InfoTab(
    patient: com.example.psymed.domain.model.PatientProfile?,
    sessionsCount: Int,
    medicationsCount: Int,
    tasksCount: Int,
    moodStates: List<com.example.psymed.domain.model.MoodState>,
    biologicalFunctions: List<com.example.psymed.domain.model.BiologicalFunctions>
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

        item {
            MoodStatisticsCard(moodStates = moodStates)
        }

        item {
            BiologicalStatisticsCard(biologicalFunctions = biologicalFunctions)
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
        icon = Icons.Outlined.TrendingUp
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
private fun MoodStatisticsCard(
    moodStates: List<com.example.psymed.domain.model.MoodState>
) {
    // Get last 7 days of mood data
    val now = LocalDate.now()
    val last7Days = moodStates.filter { mood ->
        mood.recordedAt != null && 
        !mood.recordedAt!!.isAfter(now) && 
        ChronoUnit.DAYS.between(mood.recordedAt, now) <= 7
    }.sortedByDescending { it.recordedAt }

    InfoCard(
        title = "Emotional State",
        icon = Icons.Outlined.Mood
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(0.dp))
            Box(
                modifier = Modifier
                    .background(
                        PsyMedColors.PrimaryLightest,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Last 7 days",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PsyMedColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (last7Days.isEmpty()) {
            Text(
                text = "No emotional state records yet",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PsyMedColors.TextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            // Mood distribution
            MoodDistribution(moods = last7Days)
            Spacer(modifier = Modifier.height(16.dp))
            // Recent moods list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                last7Days.take(5).forEach { mood ->
                    MoodItem(mood = mood)
                }
            }
        }
    }
}

@Composable
private fun MoodDistribution(moods: List<com.example.psymed.domain.model.MoodState>) {
    // Count each mood type
    val moodCounts = mutableMapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0)
    moods.forEach { mood ->
        moodCounts[mood.mood] = (moodCounts[mood.mood] ?: 0) + 1
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PsyMedColors.Background, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MoodCount(emoji = "😢", count = moodCounts[1] ?: 0, color = Color(0xFF2196F3))
            MoodCount(emoji = "😕", count = moodCounts[2] ?: 0, color = Color(0xFF607D8B))
            MoodCount(emoji = "😐", count = moodCounts[3] ?: 0, color = Color(0xFF9E9E9E))
            MoodCount(emoji = "😊", count = moodCounts[4] ?: 0, color = Color(0xFFFF9800))
            MoodCount(emoji = "😄", count = moodCounts[5] ?: 0, color = Color(0xFF4CAF50))
        }
    }
}

@Composable
private fun MoodCount(emoji: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}

@Composable
private fun MoodItem(mood: com.example.psymed.domain.model.MoodState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mood.getMoodEmoji(),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mood.getMoodLabel(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PsyMedColors.Primary
                )
            )
            mood.recordedAt?.let { date ->
                Text(
                    text = date.format(ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PsyMedColors.TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun BiologicalStatisticsCard(
    biologicalFunctions: List<com.example.psymed.domain.model.BiologicalFunctions>
) {
    // Get current month data
    val now = LocalDate.now()
    val currentMonthData = biologicalFunctions.filter { bio ->
        bio.recordedAt != null &&
        bio.recordedAt!!.year == now.year &&
        bio.recordedAt!!.month == now.month
    }

    // Calculate averages
    val hungerAvg = if (currentMonthData.isNotEmpty()) {
        currentMonthData.map { it.hunger }.average()
    } else 0.0
    val hydrationAvg = if (currentMonthData.isNotEmpty()) {
        currentMonthData.map { it.hydration }.average()
    } else 0.0
    val sleepAvg = if (currentMonthData.isNotEmpty()) {
        currentMonthData.map { it.sleep }.average()
    } else 0.0
    val energyAvg = if (currentMonthData.isNotEmpty()) {
        currentMonthData.map { it.energy }.average()
    } else 0.0

    InfoCard(
        title = "Physical State",
        icon = Icons.Outlined.Favorite
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(0.dp))
            Box(
                modifier = Modifier
                    .background(
                        PsyMedColors.Error.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = now.format(ofPattern("MMM yyyy")),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PsyMedColors.Error,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (currentMonthData.isEmpty()) {
            Text(
                text = "No physical state records yet",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PsyMedColors.TextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BiologicalIndicator(
                    emoji = "🍽️",
                    label = "Hunger",
                    value = hungerAvg,
                    color = Color(0xFFFF9800)
                )
                BiologicalIndicator(
                    emoji = "💧",
                    label = "Hydration",
                    value = hydrationAvg,
                    color = Color(0xFF2196F3)
                )
                BiologicalIndicator(
                    emoji = "😴",
                    label = "Sleep",
                    value = sleepAvg,
                    color = Color(0xFF3F51B5)
                )
                BiologicalIndicator(
                    emoji = "⚡",
                    label = "Energy",
                    value = energyAvg,
                    color = Color(0xFFFFEB3B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PsyMedColors.Background, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Based on ${currentMonthData.size} record${if (currentMonthData.size != 1) "s" else ""} this month",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PsyMedColors.TextSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun BiologicalIndicator(
    emoji: String,
    label: String,
    value: Double,
    color: Color
) {
    val percentage = (value / 5.0).coerceIn(0.0, 1.0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PsyMedColors.Primary
                    )
                )
                Text(
                    text = "${String.format("%.1f", value)}/5.0",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.LinearProgressIndicator(
                progress = percentage.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun MedicationsTab(
    patientId: Int,
    uiState: com.example.psymed.ui.medication.MedicationsUiState,
    viewModel: MedicationsViewModel,
    onRefresh: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Medication?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Medication?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && uiState.medications.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PsyMedColors.Primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading medications...", color = PsyMedColors.TextSecondary)
                }
            }
            uiState.error != null && uiState.medications.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading medications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
            uiState.medications.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = PsyMedColors.TextLight,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No medications assigned",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to add a medication",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.medications, key = { it.id }) { medication ->
                        MedicationCard(
                            medication = medication,
                            onEdit = { showEditDialog = medication },
                            onDelete = { showDeleteDialog = medication }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PsyMedColors.Primary
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add medication",
                tint = Color.White
            )
        }
    }

    if (showAddDialog) {
        AddMedicationDialog(
            patientId = patientId,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description, interval, quantity ->
                viewModel.createMedication(
                    MedicationRequest(
                        name = name,
                        description = description,
                        patientId = patientId,
                        interval = interval,
                        quantity = quantity
                    )
                ) { success, error ->
                    if (success) {
                        showAddDialog = false
                        onRefresh()
                    }
                }
            }
        )
    }

    showEditDialog?.let { medication ->
        EditMedicationDialog(
            medication = medication,
            onDismiss = { showEditDialog = null },
            onConfirm = { name, description, interval, quantity ->
                viewModel.updateMedication(
                    medication.id,
                    MedicationUpdateRequest(
                        name = name,
                        description = description,
                        interval = interval,
                        quantity = quantity
                    )
                ) { success, error ->
                    if (success) {
                        showEditDialog = null
                        onRefresh()
                    }
                }
            }
        )
    }

    showDeleteDialog?.let { medication ->
        DeleteMedicationDialog(
            medication = medication,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteMedication(medication.id) { success, error ->
                    if (success) {
                        showDeleteDialog = null
                        onRefresh()
                    }
                }
            }
        )
    }
}

@Composable
private fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                tint = PsyMedColors.Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = medication.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = PsyMedColors.Primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        MedicationDetailRow(label = "Interval", value = medication.interval)
        Spacer(modifier = Modifier.height(6.dp))
        MedicationDetailRow(label = "Amount", value = medication.quantity)
    }
}

@Composable
private fun MedicationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun AddMedicationDialog(
    patientId: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var interval by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Add Medication", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = interval,
                    onValueChange = { interval = it },
                    label = { Text("Interval (e.g., Every 8 hours) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (e.g., 1 tablet) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && description.isNotBlank() && interval.isNotBlank() && quantity.isNotBlank()) {
                        onConfirm(name, description, interval, quantity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun EditMedicationDialog(
    medication: Medication,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(medication.name) }
    var description by remember { mutableStateOf(medication.description) }
    var interval by remember { mutableStateOf(medication.interval) }
    var quantity by remember { mutableStateOf(medication.quantity) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Edit Medication", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = interval,
                    onValueChange = { interval = it },
                    label = { Text("Interval (e.g., Every 8 hours) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (e.g., 1 tablet) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && description.isNotBlank() && interval.isNotBlank() && quantity.isNotBlank()) {
                        onConfirm(name, description, interval, quantity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
            ) {
                Text("Update", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun DeleteMedicationDialog(
    medication: Medication,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Delete Medication", fontWeight = FontWeight.Bold, color = Color.Red)
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete this medication?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PsyMedColors.Primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = medication.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "⚠️ This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Red,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun SessionsTab(
    patientId: Int,
    professionalId: Int?,
    uiState: com.example.psymed.ui.appointments.SessionsUiState,
    viewModel: SessionsViewModel,
    onRefresh: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Session?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Session?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && uiState.sessions.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PsyMedColors.Primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading sessions...", color = PsyMedColors.TextSecondary)
                }
            }
            uiState.error != null && uiState.sessions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading sessions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
            uiState.sessions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = PsyMedColors.TextLight,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No sessions scheduled",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to schedule a session",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.sessions, key = { it.id }) { session ->
                        SessionCard(
                            session = session,
                            onEdit = { if (session.isFuture) showEditDialog = session },
                            onDelete = { showDeleteDialog = session }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PsyMedColors.Primary
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add session",
                tint = Color.White
            )
        }
    }

    if (showAddDialog && professionalId != null) {
        AddSessionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { dateTime, duration ->
                viewModel.createSession(
                    professionalId,
                    patientId,
                    SessionCreateRequest(
                        appointmentDate = dateTime,
                        sessionTime = duration
                    )
                ) { success, error ->
                    if (success) {
                        showAddDialog = false
                        onRefresh()
                    }
                }
            }
        )
    }

    showEditDialog?.let { session ->
        if (professionalId != null) {
            EditSessionDialog(
                session = session,
                onDismiss = { showEditDialog = null },
                onConfirm = { dateTime, duration ->
                    viewModel.updateSession(
                        professionalId,
                        patientId,
                        session.id,
                        SessionUpdateRequest(
                            appointmentDate = dateTime,
                            sessionTime = duration
                        )
                    ) { success, error ->
                        if (success) {
                            showEditDialog = null
                            onRefresh()
                        }
                    }
                }
            )
        }
    }

    showDeleteDialog?.let { session ->
        if (professionalId != null) {
            DeleteSessionDialog(
                session = session,
                onDismiss = { showDeleteDialog = null },
                onConfirm = {
                    viewModel.deleteSession(
                        professionalId,
                        patientId,
                        session.id
                    ) { success, error ->
                        if (success) {
                            showDeleteDialog = null
                            onRefresh()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val isPast = !session.isFuture

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = if (isPast) PsyMedColors.TextLight else PsyMedColors.Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormatter.format(session.appointmentDate),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isPast) PsyMedColors.TextLight else PsyMedColors.Primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = if (isPast) PsyMedColors.TextLight else PsyMedColors.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeFormatter.format(session.appointmentDate),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isPast) PsyMedColors.TextLight else PsyMedColors.TextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${session.sessionTime}h",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isPast) PsyMedColors.TextLight else PsyMedColors.TextSecondary
                        )
                    )
                }
            }
            if (!isPast) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = PsyMedColors.Primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            } else {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun AddSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime, Double) -> Unit
) {
    val defaultDateTime = LocalDateTime.now().plusHours(1)
    var dateText by remember { mutableStateOf(defaultDateTime.toLocalDate().toString()) }
    var timeText by remember { mutableStateOf(defaultDateTime.toLocalTime().toString().substring(0, 5)) }
    var durationText by remember { mutableStateOf("1.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Schedule Session", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2024-12-25") }
                )
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = { Text("Time (HH:mm) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("14:30") }
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (hours) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("1.0") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val date = LocalDate.parse(dateText)
                        val time = LocalTime.parse(timeText)
                        val dateTime = LocalDateTime.of(date, time)
                        val duration = durationText.toDouble()
                        
                        if (dateTime.isAfter(LocalDateTime.now()) && duration > 0) {
                            onConfirm(dateTime, duration)
                        }
                    } catch (e: Exception) {
                        // Invalid input, don't proceed
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
            ) {
                Text("Schedule", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun EditSessionDialog(
    session: Session,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime, Double) -> Unit
) {
    var dateText by remember { mutableStateOf(session.appointmentDate.toLocalDate().toString()) }
    var timeText by remember { mutableStateOf(session.appointmentDate.toLocalTime().toString().substring(0, 5)) }
    var durationText by remember { mutableStateOf(session.sessionTime.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Edit Session", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = { Text("Time (HH:mm) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (hours) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val date = LocalDate.parse(dateText)
                        val time = LocalTime.parse(timeText)
                        val dateTime = LocalDateTime.of(date, time)
                        val duration = durationText.toDouble()
                        
                        if (dateTime.isAfter(LocalDateTime.now()) && duration > 0) {
                            onConfirm(dateTime, duration)
                        }
                    } catch (e: Exception) {
                        // Invalid input, don't proceed
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
            ) {
                Text("Update", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun DeleteSessionDialog(
    session: Session,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Delete Session", fontWeight = FontWeight.Bold, color = Color.Red)
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete this session?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = dateFormatter.format(session.appointmentDate),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PsyMedColors.Primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${timeFormatter.format(session.appointmentDate)} - ${session.sessionTime}h",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "⚠️ This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Red,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun TasksTab(
    patientId: Int,
    sessionsState: com.example.psymed.ui.appointments.SessionsUiState,
    uiState: com.example.psymed.ui.tasks.TasksUiState,
    viewModel: TasksViewModel,
    onToggleStatus: (Task) -> Unit,
    onRefresh: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Task?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Task?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && uiState.tasks.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PsyMedColors.Primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading tasks...", color = PsyMedColors.TextSecondary)
                }
            }
            uiState.error != null && uiState.tasks.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TaskAlt,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading tasks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
            uiState.tasks.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TaskAlt,
                        contentDescription = null,
                        tint = PsyMedColors.TextLight,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tasks assigned",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to add a task",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "task_progress") {
                        TaskProgressCard(uiState = uiState)
                    }
                    items(uiState.tasks, key = { it.id }) { task ->
                        ProfessionalTaskCard(
                            task = task,
                            sessions = sessionsState.sessions,
                            onToggleStatus = { onToggleStatus(task) },
                            onEdit = { showEditDialog = task },
                            onDelete = { showDeleteDialog = task }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (sessionsState.sessions.isEmpty()) {
                    // Show error - need sessions first
                } else {
                    showAddDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PsyMedColors.Primary
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add task",
                tint = Color.White
            )
        }
    }

    if (showAddDialog) {
        if (sessionsState.sessions.isEmpty()) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("No Sessions Available") },
                text = {
                    Text("Please create a session before adding tasks.")
                },
                confirmButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("OK", color = PsyMedColors.Primary)
                    }
                }
            )
        } else {
            AddTaskDialog(
                sessions = sessionsState.sessions,
                onDismiss = { showAddDialog = false },
                onConfirm = { sessionId, title, description ->
                    viewModel.createTask(
                        sessionId,
                        TaskRequest(title = title, description = description)
                    ) { success, error ->
                        if (success) {
                            showAddDialog = false
                            onRefresh()
                        }
                    }
                }
            )
        }
    }

    showEditDialog?.let { task ->
        EditTaskDialog(
            task = task,
            sessions = sessionsState.sessions,
            onDismiss = { showEditDialog = null },
            onConfirm = { title, description ->
                viewModel.updateTask(
                    task.sessionId,
                    task.id.toIntOrNull() ?: 0,
                    TaskRequest(title = title, description = description)
                ) { success, error ->
                    if (success) {
                        showEditDialog = null
                        onRefresh()
                    }
                }
            }
        )
    }

    showDeleteDialog?.let { task ->
        DeleteTaskDialog(
            task = task,
            sessions = sessionsState.sessions,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                val taskId = task.id.toIntOrNull() ?: 0
                if (taskId > 0) {
                    viewModel.deleteTask(task.sessionId, taskId) { success, error ->
                        if (success) {
                            showDeleteDialog = null
                            onRefresh()
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun TaskProgressCard(uiState: com.example.psymed.ui.tasks.TasksUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = PsyMedColors.PrimaryGradient,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = "Task Progress",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProgressStatItem(
                label = "Completed",
                value = uiState.completedTasks.size.toString(),
                icon = Icons.Outlined.CheckCircle
            )
            ProgressStatItem(
                label = "Pending",
                value = uiState.pendingTasks.size.toString(),
                icon = Icons.Outlined.Pending
            )
            ProgressStatItem(
                label = "Rate",
                value = "${uiState.completionRate.toInt()}%",
                icon = Icons.Outlined.TrendingUp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(uiState.completionRate.coerceIn(0.0, 100.0).toFloat() / 100f)
                    .height(8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun ProgressStatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
        )
    }
}

@Composable
private fun ProfessionalTaskCard(
    task: Task,
    sessions: List<Session>,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val session = sessions.firstOrNull { it.id == task.sessionId }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (task.isCompleted) Color(0xFF48BB78).copy(alpha = 0.1f)
                        else Color(0xFFED8936).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.Pending,
                    contentDescription = null,
                    tint = if (task.isCompleted) Color(0xFF48BB78) else Color(0xFFED8936),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) PsyMedColors.TextLight else PsyMedColors.Primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PsyMedColors.TextSecondary
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (task.isCompleted) Color(0xFF48BB78).copy(alpha = 0.12f)
                            else Color(0xFFED8936).copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (task.isCompleted) "Completed" else "Pending",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (task.isCompleted) Color(0xFF2F855A) else Color(0xFFDD6B20),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = PsyMedColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (session != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = PsyMedColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${dateFormatter.format(session.appointmentDate)} • ${timeFormatter.format(session.appointmentDate)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = PsyMedColors.TextSecondary)
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    sessions: List<Session>,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String) -> Unit
) {
    var selectedSession by remember { mutableStateOf<Session?>(sessions.firstOrNull()) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Add Task", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = selectedSession?.let {
                            "${dateFormatter.format(it.appointmentDate)} • ${timeFormatter.format(it.appointmentDate)}"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Session *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        leadingIcon = {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null)
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sessions.forEach { session ->
                            DropdownMenuItem(
                                text = {
                                    Text("${dateFormatter.format(session.appointmentDate)} • ${timeFormatter.format(session.appointmentDate)}")
                                },
                                onClick = {
                                    selectedSession = session
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Outlined.Title, contentDescription = null)
                    }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && selectedSession != null) {
                        onConfirm(selectedSession!!.id, title, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun EditTaskDialog(
    task: Task,
    sessions: List<Session>,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    val session = sessions.firstOrNull { it.id == task.sessionId }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = PsyMedColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Edit Task", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = session?.let {
                        "${dateFormatter.format(it.appointmentDate)} • ${timeFormatter.format(it.appointmentDate)}"
                    } ?: "Session ${task.sessionId}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Session") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null)
                    },
                    enabled = false
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onConfirm(title, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
            ) {
                Text("Update", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun DeleteTaskDialog(
    task: Task,
    sessions: List<Session>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val session = sessions.firstOrNull { it.id == task.sessionId }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Delete Task", fontWeight = FontWeight.Bold, color = Color.Red)
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete this task?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PsyMedColors.Primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
                        maxLines = 3,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    if (session != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${dateFormatter.format(session.appointmentDate)} • ${timeFormatter.format(session.appointmentDate)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = PsyMedColors.TextSecondary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "⚠️ This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Red,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsyMedColors.TextSecondary)
            }
        }
    )
}
