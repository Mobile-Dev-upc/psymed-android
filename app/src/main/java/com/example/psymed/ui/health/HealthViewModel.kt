package com.example.psymed.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.AnalyticsRepository
import com.example.psymed.domain.model.BiologicalFunctions
import com.example.psymed.domain.model.MoodState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HealthUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val moodStates: List<MoodState> = emptyList(),
    val biologicalFunctions: List<BiologicalFunctions> = emptyList(),
    val error: String? = null
) {
    val lastReportDate: LocalDate? =
        (moodStates.mapNotNull { it.recordedAt } + biologicalFunctions.mapNotNull { it.recordedAt })
            .maxOrNull()

    val hasReportedToday: Boolean
        get() = lastReportDate == LocalDate.now()
}

class HealthViewModel(
    private val repository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    fun loadPatientReports(patientId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val moodStates = repository.getMoodStates(patientId)
                val biologicalFunctions = repository.getBiologicalFunctions(patientId)
                moodStates to biologicalFunctions
            }.onSuccess { (moodStates, biologicalFunctions) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        moodStates = moodStates,
                        biologicalFunctions = biologicalFunctions,
                        error = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Failed to load health data"
                    )
                }
            }
        }
    }

    fun saveDailyReport(
        patientId: Int,
        mood: Int,
        hunger: Int,
        hydration: Int,
        sleep: Int,
        energy: Int,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (_uiState.value.hasReportedToday) {
            onResult(false, "You have already registered your mood today.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                val moodState = repository.createMoodState(patientId, mood)
                val bio = repository.createBiologicalFunction(patientId, hunger, hydration, sleep, energy)
                moodState to bio
            }.onSuccess { (moodState, bio) ->
                _uiState.update { current ->
                    current.copy(
                        isSaving = false,
                        moodStates = moodState?.let { current.moodStates + it } ?: current.moodStates,
                        biologicalFunctions = bio?.let { current.biologicalFunctions + it }
                            ?: current.biologicalFunctions
                    )
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSaving = false) }
                onResult(false, throwable.message ?: "Failed to save report")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

