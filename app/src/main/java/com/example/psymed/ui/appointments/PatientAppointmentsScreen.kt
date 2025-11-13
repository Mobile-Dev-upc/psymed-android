package com.example.psymed.ui.appointments

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
import androidx.compose.material.icons.outlined.CalendarToday
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
import com.example.psymed.domain.model.Session
import com.example.psymed.ui.theme.PsyMedColors
import java.time.format.DateTimeFormatter

@Composable
fun PatientAppointmentsScreen(
    modifier: Modifier = Modifier,
    uiState: SessionsUiState,
    onRefresh: () -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = PsyMedColors.Background) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(
                message = uiState.error,
                onRetry = onRefresh
            )
            uiState.sessions.isEmpty() -> EmptyState()
            else -> AppointmentsList(
                sessions = uiState.sessions,
                onRefresh = onRefresh
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = PsyMedColors.Primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading appointments...", color = PsyMedColors.TextSecondary)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
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
            modifier = Modifier.height(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error loading appointments",
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
private fun EmptyState() {
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
            modifier = Modifier.height(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No appointments scheduled",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your doctor can schedule appointments from their panel.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AppointmentsList(
    sessions: List<Session>,
    onRefresh: () -> Unit
) {
    val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("hh:mm a")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            RefreshHint(onRefresh = onRefresh)
        }
        items(sessions) { session ->
            AppointmentCard(
                session = session,
                formatterDate = formatterDate,
                formatterTime = formatterTime
            )
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
            text = "Pull to refresh appointments",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(containerColor = PsyMedColors.Primary),
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Refresh", color = Color.White)
        }
    }
}

@Composable
private fun AppointmentCard(
    session: Session,
    formatterDate: DateTimeFormatter,
    formatterTime: DateTimeFormatter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = PsyMedColors.Primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = formatterDate.format(session.appointmentDate),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatterTime.format(session.appointmentDate),
                    style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
                )
            }
            if (session.isToday) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "TODAY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PsyMedColors.Success,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Duration: ${formatDuration(session.sessionTime)}",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
        )
    }
}

private fun formatDuration(hours: Double): String {
    val intPart = hours.toInt()
    val fractional = hours - intPart
    return when {
        fractional == 0.0 -> if (intPart == 1) "1 hour" else "$intPart hours"
        else -> "${"%.1f".format(hours)} hours"
    }
}

