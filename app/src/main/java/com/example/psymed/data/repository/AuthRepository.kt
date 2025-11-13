package com.example.psymed.data.repository

import com.example.psymed.data.api.PsyMedApi
import com.example.psymed.data.model.SignInRequestDto
import com.example.psymed.data.model.SignUpRequestDto
import com.example.psymed.data.model.toDomain
import com.example.psymed.data.model.toDto
import com.example.psymed.domain.model.AuthData
import com.example.psymed.domain.model.PatientProfile
import com.example.psymed.domain.model.PatientProfileRequest
import com.example.psymed.domain.model.PatientSummary
import com.example.psymed.domain.model.ProfessionalProfile
import com.example.psymed.domain.model.ProfessionalProfileRequest
import com.example.psymed.domain.model.UserAccount
import com.example.psymed.domain.model.UpdatePatientProfileRequest

class AuthRepository(
    private val api: PsyMedApi
) {

    suspend fun signIn(username: String, password: String): AuthData =
        api.signIn(SignInRequestDto(username = username, password = password)).toDomain()

    suspend fun signUp(username: String, password: String, role: String): UserAccount =
        api.signUp(SignUpRequestDto(username = username, password = password, role = role)).toDomain()

    suspend fun getAccount(accountId: Int): UserAccount =
        api.getAccount(accountId).toDomain()

    suspend fun getPatientProfileByAccount(accountId: Int): PatientProfile? =
        api.getPatientProfileByAccount(accountId).toDomain()

    suspend fun getProfessionalProfileByAccount(accountId: Int): ProfessionalProfile? =
        api.getProfessionalProfileByAccount(accountId).toDomain()

    suspend fun getPatientProfileById(patientId: Int): PatientProfile? =
        api.getPatientProfileById(patientId).toDomain()

    suspend fun createPatientProfile(request: PatientProfileRequest): PatientProfile? =
        api.createPatientProfile(request.toDto()).toDomain()

    suspend fun updatePatientProfile(patientId: Int, request: UpdatePatientProfileRequest): PatientProfile? =
        api.updatePatientProfile(patientId, request.toDto()).toDomain()

    suspend fun deletePatientProfile(patientId: Int) {
        api.deletePatientProfile(patientId)
    }

    suspend fun createProfessionalProfile(request: ProfessionalProfileRequest): ProfessionalProfile? =
        api.createProfessionalProfile(request.toDto()).toDomain()

    suspend fun getPatientsByProfessional(professionalId: Int): List<PatientSummary> =
        api.getPatientsByProfessional(professionalId).mapNotNull { it.toDomain() }
}

