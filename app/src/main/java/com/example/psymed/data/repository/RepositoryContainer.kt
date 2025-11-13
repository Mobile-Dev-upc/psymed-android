package com.example.psymed.data.repository

import com.example.psymed.data.api.ApiClient

class RepositoryContainer(
    tokenProvider: () -> String?
) {
    private val api = ApiClient(tokenProvider).api

    val authRepository = AuthRepository(api)
    val sessionRepository = SessionRepository(api)
    val taskRepository = TaskRepository(api)
    val medicationRepository = MedicationRepository(api)
    val analyticsRepository = AnalyticsRepository(api)
}

