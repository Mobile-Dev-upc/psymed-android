package com.example.psymed.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.MedicationRepository
import com.example.psymed.domain.model.Medication
import com.example.psymed.domain.model.MedicationRequest
import com.example.psymed.domain.model.MedicationUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationsUiState(
    val isLoading: Boolean = false,
    val medications: List<Medication> = emptyList(),
    val error: String? = null
) {
    val hasMedications: Boolean get() = medications.isNotEmpty()
}

class MedicationsViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationsUiState())
    val uiState: StateFlow<MedicationsUiState> = _uiState.asStateFlow()

    fun loadMedications(patientId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.getMedicationsByPatient(patientId)
            }.onSuccess { medications ->
                _uiState.update { it.copy(isLoading = false, medications = medications, error = null) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        medications = emptyList(),
                        error = throwable.message ?: "Failed to load medications"
                    )
                }
            }
        }
    }

    fun createMedication(
        request: MedicationRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.createMedication(request)
            }.onSuccess { medication ->
                if (medication != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            medications = (it.medications + medication).distinctBy { item -> item.id }
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to create medication")
            }
        }
    }

    fun updateMedication(
        medicationId: Int,
        request: MedicationUpdateRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.updateMedication(medicationId, request)
            }.onSuccess { medication ->
                if (medication != null) {
                    val updated = _uiState.value.medications.map {
                        if (it.id == medication.id) medication else it
                    }
                    _uiState.update { it.copy(isLoading = false, medications = updated) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to update medication")
            }
        }
    }

    fun deleteMedication(
        medicationId: Int,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.deleteMedication(medicationId)
            }.onSuccess {
                val updated = _uiState.value.medications.filterNot { it.id == medicationId }
                _uiState.update { it.copy(isLoading = false, medications = updated) }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to delete medication")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

