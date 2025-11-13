package com.example.psymed.domain.model

data class AuthData(
    val id: Int,
    val role: String,
    val token: String
)

data class UserAccount(
    val id: Int,
    val username: String,
    val role: String
)

data class PatientProfile(
    val id: Int,
    val fullName: String,
    val email: String,
    val streetAddress: String,
    val accountId: Int,
    val professionalId: Int
)

data class PatientProfileRequest(
    val firstName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val country: String,
    val email: String,
    val username: String,
    val password: String,
    val professionalId: Int
)

data class UpdatePatientProfileRequest(
    val firstName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val country: String,
    val email: String
)

data class ProfessionalProfile(
    val id: Int,
    val fullName: String,
    val email: String,
    val streetAddress: String,
    val accountId: Int
)

data class ProfessionalProfileRequest(
    val firstName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val country: String,
    val email: String,
    val username: String,
    val password: String
)

data class PatientSummary(
    val id: Int,
    val fullName: String,
    val email: String,
    val streetAddress: String,
    val accountId: Int
)

