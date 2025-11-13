package com.example.psymed.domain.model

data class Medication(
    val id: Int,
    val name: String,
    val description: String,
    val patientId: Int,
    val interval: String,
    val quantity: String
)

data class MedicationRequest(
    val name: String,
    val description: String,
    val patientId: Int,
    val interval: String,
    val quantity: String
)

data class MedicationUpdateRequest(
    val name: String,
    val description: String,
    val interval: String,
    val quantity: String
)

