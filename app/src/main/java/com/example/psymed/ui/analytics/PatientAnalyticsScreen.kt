package com.example.psymed.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.psymed.domain.model.BiologicalAnalytic
import com.example.psymed.domain.model.BiologicalFunctions
import com.example.psymed.domain.model.MoodAnalytic
import com.example.psymed.domain.model.MoodState
import com.example.psymed.ui.theme.PsyMedColors
import java.time.format.DateTimeFormatter

@Composable
fun PatientAnalyticsScreen(
    modifier: Modifier = Modifier,
    uiState: AnalyticsUiState,
    onRefresh: () -> Unit
) {
    Surface(modifier = modifier.fillMaxSize(), color = PsyMedColors.Background) {
        when {
            uiState.isLoading -> AnalyticsLoading()
            uiState.moodStates.isEmpty() && uiState.biologicalFunctions.isEmpty() -> AnalyticsEmpty()
            else -> AnalyticsContent(
                moodAnalytic = uiState.moodAnalytic,
                moodStates = uiState.moodStates,
                biologicalAnalytic = uiState.biologicalAnalytic,
                biologicalFunctions = uiState.biologicalFunctions
            )
        }
    }
}

@Composable
private fun AnalyticsLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = PsyMedColors.Primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading analytics...", color = PsyMedColors.TextSecondary)
    }
}

@Composable
private fun AnalyticsEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No analytics available yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Log your mood and physical state to view insights.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AnalyticsContent(
    moodAnalytic: MoodAnalytic?,
    moodStates: List<MoodState>,
    biologicalAnalytic: BiologicalAnalytic?,
    biologicalFunctions: List<BiologicalFunctions>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        moodAnalytic?.let {
            item {
                MoodDistributionCard(moodAnalytic = it)
            }
        }
        if (moodStates.isNotEmpty()) {
            item {
                SectionTitle("Recent Mood Entries")
            }
            items(moodStates.take(10), key = { it.id }) { mood ->
                MoodEntryRow(moodState = mood)
            }
        }

        biologicalAnalytic?.let {
            item { BiologicalAveragesCard(it) }
        }

        if (biologicalFunctions.isNotEmpty()) {
            item { SectionTitle("Recent Physical Entries") }
            items(biologicalFunctions.take(10), key = { it.id }) { entry ->
                BiologicalEntryRow(entry = entry)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            color = PsyMedColors.Primary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun MoodDistributionCard(moodAnalytic: MoodAnalytic) {
    val total = moodAnalytic.totalMoods.coerceAtLeast(1)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mood Distribution",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        MoodDistributionRow("So Sad", moodAnalytic.soSadMood, total, Color(0xFFEF4444))
        MoodDistributionRow("Sad", moodAnalytic.sadMood, total, Color(0xFFF97316))
        MoodDistributionRow("Neutral", moodAnalytic.neutralMood, total, Color(0xFF6B7280))
        MoodDistributionRow("Happy", moodAnalytic.happyMood, total, Color(0xFF10B981))
        MoodDistributionRow("So Happy", moodAnalytic.soHappyMood, total, Color(0xFF3B82F6))
    }
}

@Composable
private fun MoodDistributionRow(label: String, count: Int, total: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(
                text = "${count} (${(count * 100 / total)}%)",
                style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
            )
        }
        LinearProgressIndicator(
            progress = count.toFloat() / total.toFloat(),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MoodEntryRow(moodState: MoodState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "${moodState.getMoodEmoji()} ${moodState.getMoodLabel()}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        moodState.recordedAt?.let {
            Text(
                text = it.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                style = MaterialTheme.typography.bodySmall.copy(color = PsyMedColors.TextSecondary)
            )
        }
    }
}

@Composable
private fun BiologicalAveragesCard(analytic: BiologicalAnalytic) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Monthly Averages",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BiologicalAverageItem("🍽️ Hunger", analytic.hungerAverage)
            BiologicalAverageItem("💧 Hydration", analytic.hydrationAverage)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BiologicalAverageItem("😴 Sleep", analytic.sleepAverage)
            BiologicalAverageItem("⚡ Energy", analytic.energyAverage)
        }
    }
}

@Composable
private fun BiologicalAverageItem(label: String, value: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary))
        Text(
            text = value.takeIf { !it.isNaN() }?.let { "%.1f".format(it) } ?: "-",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PsyMedColors.Primary)
        )
    }
}

@Composable
private fun BiologicalEntryRow(entry: BiologicalFunctions) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        entry.recordedAt?.let {
            Text(
                text = it.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PsyMedColors.TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricChip("🍽️", entry.hunger)
            MetricChip("💧", entry.hydration)
            MetricChip("😴", entry.sleep)
            MetricChip("⚡", entry.energy)
        }
    }
}

@Composable
private fun MetricChip(icon: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, style = MaterialTheme.typography.titleLarge)
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

