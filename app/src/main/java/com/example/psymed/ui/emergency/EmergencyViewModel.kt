package com.example.psymed.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmergencyUiState(
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val shakeEnabled: Boolean = true
)

class EmergencyViewModel(
    private val emergencyService: EmergencyService,
    private val emergencyPreferences: EmergencyPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()
    
    init {
        loadShakeEnabledStatus()
    }
    
    fun loadShakeEnabledStatus() {
        _uiState.update { 
            it.copy(shakeEnabled = emergencyPreferences.isShakeEnabled()) 
        }
    }
    
    fun toggleShakeEnabled() {
        val newValue = !_uiState.value.shakeEnabled
        emergencyPreferences.setShakeEnabled(newValue)
        _uiState.update { it.copy(shakeEnabled = newValue) }
    }
    
    suspend fun sendEmergencyAlert(
        patientId: Int,
        professionalId: Int,
        patientName: String
    ): Boolean {
        // Check cooldown period
        if (!emergencyPreferences.canSendEmergencyAlert()) {
            _uiState.update { 
                it.copy(
                    errorMessage = "Please wait a few minutes before sending another emergency alert."
                )
            }
            return false
        }
        
        _uiState.update { 
            it.copy(isSending = true, errorMessage = null) 
        }
        
        return try {
            val result = emergencyService.sendEmergencyAlert(
                patientId = patientId,
                professionalId = professionalId,
                patientName = patientName
            )
            
            when (result) {
                is EmergencyAlertResult.Success -> {
                    emergencyPreferences.setLastEmergencyTimestamp(System.currentTimeMillis())
                    _uiState.update { it.copy(isSending = false) }
                    true
                }
                is EmergencyAlertResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isSending = false,
                            errorMessage = result.message
                        )
                    }
                    false
                }
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isSending = false,
                    errorMessage = e.message ?: "Failed to send emergency alert"
                )
            }
            false
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

