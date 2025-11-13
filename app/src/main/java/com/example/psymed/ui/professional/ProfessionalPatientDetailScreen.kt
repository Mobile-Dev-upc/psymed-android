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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
                    tasksCount = tasksState.tasks.size
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
