package com.example.psymed.ui.health

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.psymed.ui.components.PsyMedPrimaryButton
import com.example.psymed.ui.theme.PsyMedColors
import kotlinx.coroutines.launch

private val moodEmojis = listOf("😢", "😟", "😐", "😊", "😄")
private val ratingCategories = listOf("Hunger", "Hydration", "Sleep Quality", "Energy Level")

@Composable
fun PatientHealthScreen(
    modifier: Modifier = Modifier,
    uiState: HealthUiState,
    onSaveReport: (mood: Int, hunger: Int, hydration: Int, sleep: Int, energy: Int, (Boolean, String?) -> Unit) -> Unit,
    onRefresh: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedMood by rememberSaveable { mutableStateOf(-1) }
    val ratings = remember {
        mutableStateMapOf<String, Int>().apply {
            ratingCategories.forEach { this[it] = 0 }
        }
    }

    fun resetForm() {
        selectedMood = -1
        ratingCategories.forEach { ratings[it] = 0 }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = PsyMedColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (uiState.hasReportedToday) {
                ReportCompleteBanner(onRefresh = onRefresh)
            }

            Text(
                text = "Log Your Mood",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { if (!uiState.hasReportedToday && !uiState.isSaving) selectedMood = it },
                disabled = uiState.hasReportedToday
            )

            ratingCategories.forEach { category ->
                RatingRow(
                    label = category,
                    selectedValue = ratings.getValue(category),
                    onValueSelected = {
                        if (!uiState.hasReportedToday && !uiState.isSaving) {
                            ratings[category] = it
                        }
                    },
                    disabled = uiState.hasReportedToday
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PsyMedPrimaryButton(
                text = when {
                    uiState.hasReportedToday -> "Already registered today"
                    else -> "Save"
                },
                loading = uiState.isSaving,
                enabled = !uiState.hasReportedToday,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (selectedMood == -1) {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Please select your mood") }
                    return@PsyMedPrimaryButton
                }
                if (ratings.values.any { it == 0 }) {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Please complete all ratings") }
                    return@PsyMedPrimaryButton
                }
                onSaveReport(
                    selectedMood + 1,
                    ratings["Hunger"] ?: 0,
                    ratings["Hydration"] ?: 0,
                    ratings["Sleep Quality"] ?: 0,
                    ratings["Energy Level"] ?: 0
                ) { success, message ->
                    coroutineScope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar("✓ Report saved successfully")
                            resetForm()
                        } else if (!message.isNullOrBlank()) {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCompleteBanner(onRefresh: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F9EA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Registration complete!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF2F855A),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "You already registered your mood today. Come back tomorrow.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF2F855A))
            )
            Text(
                text = "Tap here to refresh",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF2F855A),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x332F855A))
                    .padding(vertical = 6.dp, horizontal = 12.dp)
                    .clickable { onRefresh() }
            )
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Int,
    onMoodSelected: (Int) -> Unit,
    disabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        moodEmojis.forEachIndexed { index, emoji ->
            val isSelected = selectedMood == index
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        color = when {
                            disabled -> PsyMedColors.TextLight.copy(alpha = 0.1f)
                            isSelected -> PsyMedColors.Primary.copy(alpha = 0.2f)
                            else -> Color.White
                        }
                    )
                    .clickable(enabled = !disabled) { onMoodSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
private fun RatingRow(
    label: String,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
    disabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(5) { index ->
                val value = index + 1
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            color = when {
                                disabled -> PsyMedColors.TextLight.copy(alpha = 0.08f)
                                selectedValue == value -> PsyMedColors.Primary.copy(alpha = 0.2f)
                                else -> Color.White
                            }
                        )
                        .clickable(enabled = !disabled) { onValueSelected(value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (selectedValue == value) PsyMedColors.Primary else PsyMedColors.TextSecondary,
                            fontWeight = if (selectedValue == value) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

