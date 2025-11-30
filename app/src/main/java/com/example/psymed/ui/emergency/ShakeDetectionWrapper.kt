package com.example.psymed.ui.emergency

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psymed.di.PsyMedViewModelFactory
import com.example.psymed.ui.auth.AuthUiState

@Composable
fun ShakeDetectionWrapper(
    authState: AuthUiState,
    factory: PsyMedViewModelFactory,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val emergencyViewModel: EmergencyViewModel = viewModel(factory = factory)
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var isOverlayVisible by remember { mutableStateOf(false) }
    
    val shakeDetector = remember {
        ShakeDetector(context) {
            Log.d("ShakeDetection", "🔔 SHAKE DETECTED!")
            handleShake(
                authState = authState,
                emergencyViewModel = emergencyViewModel,
                isOverlayVisible = isOverlayVisible,
                onShowDialog = { showEmergencyDialog = true },
                onSetOverlayVisible = { isOverlayVisible = it }
            )
        }
    }
    
    // Start/stop shake detection based on authentication and user role
    LaunchedEffect(authState.isAuthenticated, authState.isPatient, emergencyViewModel.uiState.value.shakeEnabled) {
        if (authState.isAuthenticated && 
            authState.isPatient && 
            emergencyViewModel.uiState.value.shakeEnabled) {
            shakeDetector.startListening()
            Log.d("ShakeDetection", "✅ Shake detector started")
        } else {
            shakeDetector.stopListening()
            Log.d("ShakeDetection", "⏸️ Shake detector stopped")
        }
    }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            shakeDetector.stopListening()
        }
    }
    
    // Show emergency dialog
    if (showEmergencyDialog && authState.patientProfile != null) {
        EmergencyDialog(
            emergencyViewModel = emergencyViewModel,
            patientProfile = authState.patientProfile,
            onDismiss = {
                showEmergencyDialog = false
                isOverlayVisible = false
            },
            onSendAlert = { patientId, professionalId, patientName ->
                emergencyViewModel.sendEmergencyAlert(
                    patientId = patientId,
                    professionalId = professionalId,
                    patientName = patientName
                )
            }
        )
    }
    
    content()
}

private fun handleShake(
    authState: AuthUiState,
    emergencyViewModel: EmergencyViewModel,
    isOverlayVisible: Boolean,
    onShowDialog: () -> Unit,
    onSetOverlayVisible: (Boolean) -> Unit
) {
    if (isOverlayVisible) {
        Log.d("ShakeDetection", "⚠️ Overlay already visible, ignoring shake")
        return
    }
    
    Log.d("ShakeDetection", "👤 User role: ${authState.account?.role}")
    Log.d("ShakeDetection", "👤 Is patient: ${authState.isPatient}")
    
    // Only show emergency dialog for patients
    if (!authState.isPatient) {
        Log.d("ShakeDetection", "❌ Not a patient, ignoring shake")
        return
    }
    
    // Check if shake detection is enabled
    emergencyViewModel.loadShakeEnabledStatus()
    val shakeEnabled = emergencyViewModel.uiState.value.shakeEnabled
    Log.d("ShakeDetection", "🔧 Shake enabled: $shakeEnabled")
    
    if (!shakeEnabled) {
        Log.d("ShakeDetection", "❌ Shake detection disabled, ignoring shake")
        return
    }
    
    // Check if patient profile exists
    if (authState.patientProfile == null) {
        Log.d("ShakeDetection", "❌ Patient profile not found, ignoring shake")
        return
    }
    
    Log.d("ShakeDetection", "✅ All checks passed, showing emergency dialog")
    onSetOverlayVisible(true)
    onShowDialog()
}

