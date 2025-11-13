package com.example.psymed.ui.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.psymed.domain.model.Medication
import com.example.psymed.ui.theme.PsyMedColors

@Composable
fun PatientMedicationScreen(
    modifier: Modifier = Modifier,
    uiState: MedicationsUiState,
    onRefresh: () -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = PsyMedColors.Background) {
        when {
            uiState.isLoading -> MedicationLoading()
            uiState.error != null -> MedicationError(message = uiState.error, onRetry = onRefresh)
            uiState.medications.isEmpty() -> MedicationEmptyState()
            else -> MedicationList(medications = uiState.medications, onRefresh = onRefresh)
        }
    }
}

@Composable
private fun MedicationLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = PsyMedColors.Primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading medications...", color = PsyMedColors.TextSecondary)
    }
}

@Composable
private fun MedicationError(message: String, onRetry: () -> Unit) {
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
            modifier = Modifier.height(60.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error loading medications",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
        ) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
private fun MedicationEmptyState() {
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
            modifier = Modifier.height(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No medications assigned",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your doctor can assign medications from their panel.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MedicationList(
    medications: List<Medication>,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            RefreshHint(onRefresh = onRefresh)
        }
        items(medications, key = { it.id }) { medication ->
            MedicationCard(medication = medication)
        }
    }
}

@Composable
private fun RefreshHint(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PsyMedColors.PrimaryLightest,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Pull to refresh medications",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary)
        ) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Refresh", color = Color.White)
        }
    }
}

@Composable
private fun MedicationCard(medication: Medication) {
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
                tint = PsyMedColors.Primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
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

