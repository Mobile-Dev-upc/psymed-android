package com.example.psymed.domain.model

import java.time.Duration
import java.time.LocalDateTime

data class Session(
    val id: Int,
    val patientId: Int,
    val professionalId: Int,
    val appointmentDate: LocalDateTime,
    val sessionTime: Double
) {
    val isFuture: Boolean
        get() = appointmentDate.isAfter(LocalDateTime.now())

    val isToday: Boolean
        get() {
            val now = LocalDateTime.now()
            return appointmentDate.toLocalDate() == now.toLocalDate()
        }

    val timeUntilSession: Duration
        get() = Duration.between(LocalDateTime.now(), appointmentDate)
}

data class SessionCreateRequest(
    val appointmentDate: LocalDateTime,
    val sessionTime: Double
)

data class SessionUpdateRequest(
    val appointmentDate: LocalDateTime,
    val sessionTime: Double
)

