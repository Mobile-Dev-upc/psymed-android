package com.example.psymed.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.AuthRepository
import com.example.psymed.domain.model.AuthData
import com.example.psymed.domain.model.PatientProfile
import com.example.psymed.domain.model.ProfessionalProfile
import com.example.psymed.domain.model.ProfessionalProfileRequest
import com.example.psymed.domain.model.UserAccount
import com.example.psymed.domain.model.PatientProfileRequest
import com.example.psymed.domain.model.PatientSummary
import com.example.psymed.domain.model.UpdatePatientProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val authData: AuthData? = null,
    val account: UserAccount? = null,
    val patientProfile: PatientProfile? = null,
    val professionalProfile: ProfessionalProfile? = null,
    val error: String? = null
) {
    val isAuthenticated: Boolean get() = authData != null
    val token: String? get() = authData?.token
    val isProfessional: Boolean get() = account?.role == "ROLE_PROFESSIONAL"
    val isPatient: Boolean get() = account?.role == "ROLE_PATIENT"
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenUpdater: (String?) -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val auth = authRepository.signIn(username, password)
                tokenUpdater(auth.token)
                val account = authRepository.getAccount(auth.id)
                val patientProfile = if (account.role == "ROLE_PATIENT") {
                    authRepository.getPatientProfileByAccount(auth.id)
                } else null
                val professionalProfile = if (account.role == "ROLE_PROFESSIONAL") {
                    authRepository.getProfessionalProfileByAccount(auth.id)
                } else null
                _uiState.value = AuthUiState(
                    isLoading = false,
                    authData = auth,
                    account = account,
                    patientProfile = patientProfile,
                    professionalProfile = professionalProfile,
                    error = null
                )
                true
            }.onFailure { throwable ->
                tokenUpdater(null)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authData = null,
                        account = null,
                        patientProfile = null,
                        professionalProfile = null,
                        error = throwable.message ?: "Authentication error"
                    )
                }
            }
        }
    }

    fun reloadProfile() {
        val auth = _uiState.value.authData ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val account = authRepository.getAccount(auth.id)
                val patientProfile = if (account.role == "ROLE_PATIENT") {
                    authRepository.getPatientProfileByAccount(auth.id)
                } else null
                val professionalProfile = if (account.role == "ROLE_PROFESSIONAL") {
                    authRepository.getProfessionalProfileByAccount(auth.id)
                } else null
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        account = account,
                        patientProfile = patientProfile,
                        professionalProfile = professionalProfile,
                        error = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Failed to refresh profile"
                    )
                }
            }
        }
    }

    fun signOut() {
        tokenUpdater(null)
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun registerProfessional(request: ProfessionalProfileRequest, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.createProfessionalProfile(request)
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message)
            }
        }
    }

    fun createPatientForProfessional(
        request: PatientProfileRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.createPatientProfile(request)
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message)
            }
        }
    }

    fun updatePatientProfile(
        patientId: Int,
        request: UpdatePatientProfileRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.updatePatientProfile(patientId, request)
            }.onSuccess { profile ->
                val currentAuth = _uiState.value
                if (currentAuth.account?.role == "ROLE_PATIENT" && profile != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            patientProfile = profile
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message)
            }
        }
    }

    fun loadPatientsForProfessional(
        professionalId: Int,
        onResult: (List<PatientSummary>?, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.getPatientsByProfessional(professionalId)
            }.onSuccess { patients ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(patients, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(null, throwable.message)
            }
        }
    }
}

