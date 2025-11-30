package com.example.psymed.ui.professional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.AuthRepository
import com.example.psymed.domain.model.PatientProfile
import com.example.psymed.domain.model.PatientSummary
import com.example.psymed.domain.model.UpdatePatientProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfessionalPatientsUiState(
    val isLoading: Boolean = false,
    val patients: List<PatientSummary> = emptyList(),
    val selectedPatient: PatientProfile? = null,
    val error: String? = null
)

class ProfessionalPatientsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfessionalPatientsUiState())
    val uiState: StateFlow<ProfessionalPatientsUiState> = _uiState.asStateFlow()

    fun loadPatients(professionalId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.getPatientsByProfessional(professionalId)
            }.onSuccess { patients ->
                _uiState.update { it.copy(isLoading = false, patients = patients, error = null) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Failed to load patients"
                    )
                }
            }
        }
    }

    fun loadPatientDetails(patientId: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.getPatientProfileById(patientId)
            }.onSuccess { profile ->
                _uiState.update { it.copy(isLoading = false, selectedPatient = profile, error = null) }
                onResult(profile != null, if (profile == null) "Patient data unavailable" else null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to load patient details")
            }
        }
    }

    fun deletePatient(patientId: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.deletePatientProfile(patientId)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        patients = it.patients.filterNot { patient -> patient.id == patientId },
                        selectedPatient = if (it.selectedPatient?.id == patientId) null else it.selectedPatient
                    )
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to delete patient")
            }
        }
    }

    fun updatePatient(patientId: Int, request: UpdatePatientProfileRequest, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.updatePatientProfile(patientId, request)
            }.onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedPatient = profile,
                        error = null,
                        // Update the patient in the list if it exists
                        patients = it.patients.map { patient ->
                            if (patient.id == patientId && profile != null) {
                                PatientSummary(
                                    id = profile.id,
                                    fullName = profile.fullName,
                                    email = profile.email,
                                    streetAddress = profile.streetAddress,
                                    accountId = profile.accountId
                                )
                            } else {
                                patient
                            }
                        }
                    )
                }
                onResult(profile != null, if (profile == null) "Failed to update patient" else null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to update patient")
            }
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedPatient = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

