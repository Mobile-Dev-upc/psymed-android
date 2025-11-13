package com.example.psymed.data.model

import com.google.gson.annotations.SerializedName

data class SessionDto(
    @SerializedName("id") val id: Any?,
    @SerializedName("patientId") val patientId: Any?,
    @SerializedName("professionalId") val professionalId: Any?,
    @SerializedName("appointmentDate") val appointmentDate: String?,
    @SerializedName("sessionTime") val sessionTime: Any?
)

data class SessionCreateRequestDto(
    @SerializedName("appointmentDate") val appointmentDate: String,
    @SerializedName("sessionTime") val sessionTime: Double
)

data class SessionUpdateRequestDto(
    @SerializedName("appointmentDate") val appointmentDate: String,
    @SerializedName("sessionTime") val sessionTime: Double
)

