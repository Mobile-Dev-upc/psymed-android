package com.example.psymed.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.AnalyticsRepository
import com.example.psymed.domain.model.BiologicalAnalytic
import com.example.psymed.domain.model.BiologicalFunctions
import com.example.psymed.domain.model.MoodAnalytic
import com.example.psymed.domain.model.MoodState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val moodStates: List<MoodState> = emptyList(),
    val biologicalFunctions: List<BiologicalFunctions> = emptyList(),
    val moodAnalytic: MoodAnalytic? = null,
    val biologicalAnalytic: BiologicalAnalytic? = null,
    val error: String? = null
)

class AnalyticsViewModel(
    private val repository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    fun loadAnalytics(patientId: Int, year: String, month: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val moodStates = repository.getMoodStates(patientId)
                val biologicalFunctions = repository.getBiologicalFunctions(patientId)
                val moodAnalytic = repository.calculateMoodAnalytics(moodStates, patientId, year, month)
                val biologicalAnalytic = repository.calculateBiologicalAnalytics(biologicalFunctions, patientId, year, month)
                AnalyticsUiState(
                    isLoading = false,
                    moodStates = moodStates,
                    biologicalFunctions = biologicalFunctions,
                    moodAnalytic = moodAnalytic,
                    biologicalAnalytic = biologicalAnalytic,
                    error = null
                )
            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Failed to load analytics"
                    )
                }
            }
        }
    }

    fun refresh(patientId: Int, year: String, month: String) {
        loadAnalytics(patientId, year, month)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

