package com.example.psymed.ui.tasks

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.psymed.domain.model.Task
import com.example.psymed.ui.theme.PsyMedColors

@Composable
fun PatientTasksScreen(
    modifier: Modifier = Modifier,
    uiState: TasksUiState,
    onToggleStatus: (Task) -> Unit,
    onRefresh: () -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = PsyMedColors.Background) {
        when {
            uiState.isLoading -> TasksLoading()
            uiState.error != null -> TasksError(message = uiState.error!!, onRetry = onRefresh)
            uiState.tasks.isEmpty() -> TasksEmptyState()
            else -> TasksList(uiState = uiState, onToggleStatus = onToggleStatus, onRefresh = onRefresh)
        }
    }
}

@Composable
private fun TasksLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = PsyMedColors.Primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading tasks...", color = PsyMedColors.TextSecondary)
    }
}

@Composable
private fun TasksError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading tasks",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = PsyMedColors.Primary
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
private fun TasksEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tasks yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your therapist will assign tasks for you.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TasksList(
    uiState: TasksUiState,
    onToggleStatus: (Task) -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TaskProgressCard(uiState = uiState, onRefresh = onRefresh) }
        items(uiState.tasks, key = { it.id }) { task ->
            TaskCard(task = task, onToggleStatus = { onToggleStatus(task) })
        }
    }
}

@Composable
private fun TaskProgressCard(uiState: TasksUiState, onRefresh: () -> Unit) {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProgressItem(label = "Completed", value = uiState.completedTasks.size.toString())
            ProgressItem(label = "Pending", value = uiState.pendingTasks.size.toString())
            ProgressItem(label = "Rate", value = "${uiState.completionRate.toInt()}%")
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
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap to refresh",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onRefresh() }
        )
    }
}

@Composable
private fun ProgressItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
        )
    }
}

@Composable
private fun TaskCard(task: Task, onToggleStatus: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = if (task.isCompleted) Color(0xFF48BB78) else Color(0xFFED8936),
                        shape = CircleShape
                    )
                    .clickable { onToggleStatus() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) PsyMedColors.TextLight else PsyMedColors.TextPrimary
                    )
                )
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
                        maxLines = 2
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            StatusBadge(isCompleted = task.isCompleted)
        }
    }
}

@Composable
private fun StatusBadge(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isCompleted) Color(0xFF48BB78).copy(alpha = 0.15f)
                else Color(0xFFED8936).copy(alpha = 0.15f)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = if (isCompleted) "Done" else "Pending",
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isCompleted) Color(0xFF2F855A) else Color(0xFFDD6B20),
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

