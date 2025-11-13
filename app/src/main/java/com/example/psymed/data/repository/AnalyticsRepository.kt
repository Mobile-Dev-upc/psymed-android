package com.example.psymed.data.repository

import com.example.psymed.data.api.PsyMedApi
import com.example.psymed.data.model.BiologicalFunctionRequestDto
import com.example.psymed.data.model.MoodStateRequestDto
import com.example.psymed.data.model.toAnalytic
import com.example.psymed.data.model.toDomain
import com.example.psymed.domain.model.BiologicalAnalytic
import com.example.psymed.domain.model.BiologicalFunctions
import com.example.psymed.domain.model.MoodAnalytic
import com.example.psymed.domain.model.MoodState

class AnalyticsRepository(
    private val api: PsyMedApi
) {

    suspend fun getMoodStates(patientId: Int): List<MoodState> =
        api.getMoodStates(patientId).mapNotNull { it.toDomain() }

    suspend fun createMoodState(patientId: Int, mood: Int): MoodState? {
        require(mood in 1..5) { "Mood must be between 1 and 5" }
        val request = MoodStateRequestDto(status = mood - 1)
        return api.createMoodState(patientId, request).toDomain()
    }

    suspend fun getBiologicalFunctions(patientId: Int): List<BiologicalFunctions> =
        api.getBiologicalFunctions(patientId).mapNotNull { it.toDomain() }

    suspend fun createBiologicalFunction(
        patientId: Int,
        hunger: Int,
        hydration: Int,
        sleep: Int,
        energy: Int
    ): BiologicalFunctions? {
        val request = BiologicalFunctionRequestDto(
            hunger = hunger,
            hydration = hydration,
            sleep = sleep,
            energy = energy
        )
        return api.createBiologicalFunction(patientId, request).toDomain()
    }

    fun calculateMoodAnalytics(
        moodStates: List<MoodState>,
        patientId: Int,
        year: String,
        month: String
    ): MoodAnalytic =
        moodStates.toAnalytic(year = year, month = month, patientId = patientId)

    fun calculateBiologicalAnalytics(
        biologicalFunctions: List<BiologicalFunctions>,
        patientId: Int,
        year: String,
        month: String
    ): BiologicalAnalytic =
        biologicalFunctions.toAnalytic(year = year, month = month, patientId = patientId)
}

