package com.example.psymed.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.psymed.data.repository.RepositoryContainer
import android.content.Context
import com.example.psymed.data.api.ApiClient
import com.example.psymed.data.ServiceLocator
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.auth.AuthViewModel
import com.example.psymed.ui.emergency.EmergencyPreferences
import com.example.psymed.ui.emergency.EmergencyService
import com.example.psymed.ui.emergency.EmergencyViewModel
import com.example.psymed.ui.health.HealthViewModel
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.professional.ProfessionalPatientsViewModel
import com.example.psymed.ui.tasks.TasksViewModel

class PsyMedViewModelFactory(
    private val repositories: RepositoryContainer,
    private val tokenUpdater: (String?) -> Unit,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(repositories.authRepository, tokenUpdater) as T

            modelClass.isAssignableFrom(SessionsViewModel::class.java) ->
                SessionsViewModel(repositories.sessionRepository) as T

            modelClass.isAssignableFrom(MedicationsViewModel::class.java) ->
                MedicationsViewModel(repositories.medicationRepository) as T

            modelClass.isAssignableFrom(TasksViewModel::class.java) ->
                TasksViewModel(repositories.taskRepository) as T

            modelClass.isAssignableFrom(HealthViewModel::class.java) ->
                HealthViewModel(repositories.analyticsRepository) as T

            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) ->
                AnalyticsViewModel(repositories.analyticsRepository) as T

            modelClass.isAssignableFrom(ProfessionalPatientsViewModel::class.java) ->
                ProfessionalPatientsViewModel(repositories.authRepository) as T

            modelClass.isAssignableFrom(EmergencyViewModel::class.java) -> {
                // ApiClient needs a token provider function, not a token updater
                val tokenProvider: () -> String? = { ServiceLocator.tokenState.value }
                val apiClient = ApiClient(tokenProvider)
                val emergencyService = EmergencyService(apiClient.api)
                val emergencyPreferences = context?.let { EmergencyPreferences(it) }
                    ?: throw IllegalStateException("Context required for EmergencyViewModel")
                EmergencyViewModel(emergencyService, emergencyPreferences) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

