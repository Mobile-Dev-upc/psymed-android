package com.example.psymed.domain.model

import java.time.LocalDateTime

data class Task(
    val id: String,
    val patientId: Int,
    val sessionId: Int,
    val title: String,
    val description: String,
    val status: Int,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    val isCompleted: Boolean get() = status == 1
}

data class TaskRequest(
    val title: String,
    val description: String
)

