package com.example.psymed.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class PatientProfileRequestDto(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("professionalId") val professionalId: Int
)

data class UpdatePatientProfileRequestDto(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String,
    @SerializedName("email") val email: String
)

data class ProfessionalProfileRequestDto(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class PatientProfileDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("streetAddress") val streetAddress: String?,
    @SerializedName("accountId") val accountId: JsonElement?,
    @SerializedName("professionalId") val professionalId: Int?
)

data class ProfessionalProfileDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("streetAddress") val streetAddress: String?,
    @SerializedName("accountId") val accountId: JsonElement?
)

data class PatientSummaryDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("streetAddress") val streetAddress: String?,
    @SerializedName("accountId") val accountId: JsonElement?
)

