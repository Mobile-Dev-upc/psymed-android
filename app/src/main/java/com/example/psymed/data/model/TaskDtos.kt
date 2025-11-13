package com.example.psymed.data.model

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("taskId") val taskId: Any?,
    @SerializedName("id") val id: Any?,
    @SerializedName("idPatient") val idPatient: Any?,
    @SerializedName("idSession") val idSession: Any?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: Any?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class TaskRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)

