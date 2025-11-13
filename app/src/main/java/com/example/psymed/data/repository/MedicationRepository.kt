package com.example.psymed.data.repository

import com.example.psymed.data.api.PsyMedApi
import com.example.psymed.data.model.toDomain
import com.example.psymed.data.model.toDto
import com.example.psymed.domain.model.Medication
import com.example.psymed.domain.model.MedicationRequest
import com.example.psymed.domain.model.MedicationUpdateRequest

class MedicationRepository(
    private val api: PsyMedApi
) {

    suspend fun getMedicationsByPatient(patientId: Int): List<Medication> =
        api.getMedicationsByPatient(patientId).mapNotNull { it.toDomain() }

    suspend fun createMedication(request: MedicationRequest): Medication? =
        api.createMedication(request.toDto()).toDomain()

    suspend fun updateMedication(medicationId: Int, request: MedicationUpdateRequest): Medication? =
        api.updateMedication(medicationId, request.toDto()).toDomain()

    suspend fun deleteMedication(medicationId: Int) {
        api.deleteMedication(medicationId)
    }
}

