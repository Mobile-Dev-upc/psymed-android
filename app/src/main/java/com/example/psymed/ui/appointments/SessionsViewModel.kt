package com.example.psymed.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.SessionRepository
import com.example.psymed.domain.model.Session
import com.example.psymed.domain.model.SessionCreateRequest
import com.example.psymed.domain.model.SessionUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionsUiState(
    val isLoading: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val error: String? = null
)

class SessionsViewModel(
    private val repository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    fun loadPatientSessions(patientId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.getSessionsByPatient(patientId)
            }.onSuccess { sessions ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sessions = sessions.sortedBy { session -> session.appointmentDate }
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Failed to load sessions"
                    )
                }
            }
        }
    }

    fun createSession(
        professionalId: Int,
        patientId: Int,
        request: SessionCreateRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.createSession(professionalId, patientId, request)
            }.onSuccess { session ->
                if (session != null) {
                    val updated = (_uiState.value.sessions + session)
                        .distinctBy { it.id }
                        .sortedBy { it.appointmentDate }
                    _uiState.update { it.copy(isLoading = false, sessions = updated) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to create session")
            }
        }
    }

    fun updateSession(
        professionalId: Int,
        patientId: Int,
        sessionId: Int,
        request: SessionUpdateRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.updateSession(professionalId, patientId, sessionId, request)
            }.onSuccess { session ->
                if (session != null) {
                    val updated = _uiState.value.sessions.map {
                        if (it.id == session.id) session else it
                    }.sortedBy { it.appointmentDate }
                    _uiState.update { it.copy(isLoading = false, sessions = updated) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to update session")
            }
        }
    }

    fun deleteSession(
        professionalId: Int,
        patientId: Int,
        sessionId: Int,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.deleteSession(professionalId, patientId, sessionId)
            }.onSuccess {
                val updated = _uiState.value.sessions.filterNot { it.id == sessionId }
                _uiState.update { it.copy(isLoading = false, sessions = updated) }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to delete session")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

