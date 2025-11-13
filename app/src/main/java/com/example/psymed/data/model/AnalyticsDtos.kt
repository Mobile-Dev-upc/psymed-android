package com.example.psymed.data.model

import com.google.gson.annotations.SerializedName

data class MoodStateDto(
    @SerializedName("id") val id: Any?,
    @SerializedName("idPatient") val idPatient: Any?,
    @SerializedName("status") val status: Any?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("recordDate") val recordDate: String?
)

data class MoodStateRequestDto(
    @SerializedName("status") val status: Int
)

data class BiologicalFunctionDto(
    @SerializedName("id") val id: Any?,
    @SerializedName("hunger") val hunger: Any?,
    @SerializedName("hydration") val hydration: Any?,
    @SerializedName("sleep") val sleep: Any?,
    @SerializedName("energy") val energy: Any?,
    @SerializedName("idPatient") val idPatient: Any?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("recordDate") val recordDate: String?
)

data class BiologicalFunctionRequestDto(
    @SerializedName("hunger") val hunger: Int,
    @SerializedName("hydration") val hydration: Int,
    @SerializedName("sleep") val sleep: Int,
    @SerializedName("energy") val energy: Int
)

