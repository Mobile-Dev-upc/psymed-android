package com.example.psymed.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.psymed.data.repository.RepositoryContainer
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.auth.AuthViewModel
import com.example.psymed.ui.health.HealthViewModel
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.professional.ProfessionalPatientsViewModel
import com.example.psymed.ui.tasks.TasksViewModel

class PsyMedViewModelFactory(
    private val repositories: RepositoryContainer,
    private val tokenUpdater: (String?) -> Unit
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

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

