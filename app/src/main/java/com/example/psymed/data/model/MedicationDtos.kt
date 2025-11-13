package com.example.psymed.data.model

import com.google.gson.annotations.SerializedName

data class MedicationDto(
    @SerializedName("id") val id: Any?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("patientId") val patientId: Any?,
    @SerializedName("interval") val interval: String?,
    @SerializedName("quantity") val quantity: String?
)

data class MedicationRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("patientId") val patientId: Int,
    @SerializedName("interval") val interval: String,
    @SerializedName("quantity") val quantity: String
)

data class MedicationUpdateRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("interval") val interval: String,
    @SerializedName("quantity") val quantity: String
)

