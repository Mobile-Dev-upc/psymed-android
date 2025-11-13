package com.example.psymed.data.repository

import com.example.psymed.data.api.PsyMedApi
import com.example.psymed.data.model.toDomain
import com.example.psymed.data.model.toDto
import com.example.psymed.domain.model.Session
import com.example.psymed.domain.model.SessionCreateRequest
import com.example.psymed.domain.model.SessionUpdateRequest

class SessionRepository(
    private val api: PsyMedApi
) {

    suspend fun getSessionsByPatient(patientId: Int): List<Session> =
        api.getSessionsByPatient(patientId).mapNotNull { it.toDomain() }

    suspend fun createSession(
        professionalId: Int,
        patientId: Int,
        request: SessionCreateRequest
    ): Session? = api.createSession(
        professionalId = professionalId,
        patientId = patientId,
        request = request.toDto()
    ).toDomain()

    suspend fun updateSession(
        professionalId: Int,
        patientId: Int,
        sessionId: Int,
        request: SessionUpdateRequest
    ): Session? = api.updateSession(
        professionalId = professionalId,
        patientId = patientId,
        sessionId = sessionId,
        request = request.toDto()
    ).toDomain()

    suspend fun deleteSession(
        professionalId: Int,
        patientId: Int,
        sessionId: Int
    ) {
        api.deleteSession(
            professionalId = professionalId,
            patientId = patientId,
            sessionId = sessionId
        )
    }
}

