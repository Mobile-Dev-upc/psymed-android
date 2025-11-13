package com.example.psymed.data.model

import com.example.psymed.domain.model.AuthData
import com.example.psymed.domain.model.BiologicalAnalytic
import com.example.psymed.domain.model.BiologicalFunctions
import com.example.psymed.domain.model.Medication
import com.example.psymed.domain.model.MedicationRequest
import com.example.psymed.domain.model.MedicationUpdateRequest
import com.example.psymed.domain.model.MoodAnalytic
import com.example.psymed.domain.model.MoodState
import com.example.psymed.domain.model.PatientProfile
import com.example.psymed.domain.model.PatientProfileRequest
import com.example.psymed.domain.model.PatientSummary
import com.example.psymed.domain.model.ProfessionalProfile
import com.example.psymed.domain.model.ProfessionalProfileRequest
import com.example.psymed.domain.model.Session
import com.example.psymed.domain.model.SessionCreateRequest
import com.example.psymed.domain.model.SessionUpdateRequest
import com.example.psymed.domain.model.Task
import com.example.psymed.domain.model.TaskRequest
import com.example.psymed.domain.model.UserAccount
import com.example.psymed.domain.model.UpdatePatientProfileRequest
import com.google.gson.JsonElement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

private val dateTimeParsers = listOf(
    DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    DateTimeFormatter.ISO_ZONED_DATE_TIME,
    DateTimeFormatter.ISO_INSTANT,
    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
)

private val dateParsers = listOf(
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ISO_DATE,
    DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
)

private fun parseLocalDateTime(value: String?): LocalDateTime? {
    if (value.isNullOrBlank()) return null
    dateTimeParsers.forEach { formatter ->
        runCatching {
            return when (formatter) {
                DateTimeFormatter.ISO_INSTANT -> LocalDateTime.ofInstant(
                    formatter.parse(value, java.time.Instant::from),
                    ZoneId.systemDefault()
                )
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                DateTimeFormatter.ISO_ZONED_DATE_TIME -> LocalDateTime.ofInstant(
                    formatter.parse(value, java.time.OffsetDateTime::from).toInstant(),
                    ZoneId.systemDefault()
                )
                else -> LocalDateTime.parse(value, formatter)
            }
        }
    }
    return null
}

private fun parseLocalDate(value: String?): LocalDate? {
    if (value.isNullOrBlank()) return null
    val slice = value.replace("T", " ").take(min(10, value.length))
    dateParsers.forEach { formatter ->
        runCatching {
            return LocalDate.parse(slice, formatter)
        }
    }
    return runCatching { LocalDate.parse(slice) }.getOrNull()
}

private fun Any?.asIntOrNull(): Int? = when (this) {
    null -> null
    is Number -> toInt()
    is String -> toDoubleOrNull()?.toInt()
    else -> null
}

private fun Any?.asDoubleOrNull(): Double? = when (this) {
    null -> null
    is Number -> toDouble()
    is String -> toDoubleOrNull()
    else -> null
}

private fun JsonElement?.asIntOrNull(): Int? = this?.let {
    when {
        it.isJsonNull -> null
        it.isJsonPrimitive && it.asJsonPrimitive.isNumber -> runCatching { it.asInt }.getOrNull()
        it.isJsonPrimitive && it.asJsonPrimitive.isString -> it.asString.toDoubleOrNull()?.toInt()
        it.isJsonObject && it.asJsonObject.has("accountId") ->
            it.asJsonObject["accountId"].asIntOrNull()
        else -> null
    }
}

private fun JsonElement?.asIntOrDefault(default: Int = 0): Int = asIntOrNull() ?: default

private fun LocalDateTime.toBackendIsoString(): String =
    this.atZone(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

fun AuthResponseDto.toDomain(): AuthData =
    AuthData(id = id, role = role, token = token)

fun UserResponseDto.toDomain(): UserAccount =
    UserAccount(id = id, username = username, role = role)

fun PatientProfileDto.toDomain(): PatientProfile? {
    val resolvedId = id ?: return null
    val accountIdValue = accountId.asIntOrDefault()
    val professionalIdValue = professionalId ?: 0
    return PatientProfile(
        id = resolvedId,
        fullName = fullName.orEmpty(),
        email = email.orEmpty(),
        streetAddress = streetAddress.orEmpty(),
        accountId = accountIdValue,
        professionalId = professionalIdValue
    )
}

fun ProfessionalProfileDto.toDomain(): ProfessionalProfile? {
    val resolvedId = id ?: return null
    val accountIdValue = accountId.asIntOrDefault()
    return ProfessionalProfile(
        id = resolvedId,
        fullName = fullName.orEmpty(),
        email = email.orEmpty(),
        streetAddress = streetAddress.orEmpty(),
        accountId = accountIdValue
    )
}

fun PatientSummaryDto.toDomain(): PatientSummary? {
    val resolvedId = id ?: return null
    val accountIdValue = accountId.asIntOrDefault()
    return PatientSummary(
        id = resolvedId,
        fullName = fullName.orEmpty(),
        email = email.orEmpty(),
        streetAddress = streetAddress.orEmpty(),
        accountId = accountIdValue
    )
}

fun SessionDto.toDomain(): Session? {
    val resolvedId = id.asIntOrNull() ?: return null
    val patient = patientId.asIntOrNull() ?: return null
    val professional = professionalId.asIntOrNull() ?: 0
    val date = parseLocalDateTime(appointmentDate) ?: return null
    val duration = sessionTime.asDoubleOrNull() ?: 0.0
    return Session(
        id = resolvedId,
        patientId = patient,
        professionalId = professional,
        appointmentDate = date,
        sessionTime = duration
    )
}

fun SessionCreateRequest.toDto(): SessionCreateRequestDto =
    SessionCreateRequestDto(
        appointmentDate = appointmentDate.toBackendIsoString(),
        sessionTime = sessionTime
    )

fun SessionUpdateRequest.toDto(): SessionUpdateRequestDto =
    SessionUpdateRequestDto(
        appointmentDate = appointmentDate.toBackendIsoString(),
        sessionTime = sessionTime
    )

fun TaskDto.toDomain(): Task? {
    val resolvedId = (taskId ?: id)?.toString() ?: return null
    val patient = idPatient.asIntOrNull() ?: 0
    val session = idSession.asIntOrNull() ?: 0
    val created = parseLocalDateTime(createdAt)
    val updated = parseLocalDateTime(updatedAt)
    val statusValue = status.asIntOrNull() ?: 0
    return Task(
        id = resolvedId,
        patientId = patient,
        sessionId = session,
        title = title.orEmpty(),
        description = description.orEmpty(),
        status = statusValue,
        createdAt = created,
        updatedAt = updated
    )
}

fun TaskRequest.toDto(): TaskRequestDto = TaskRequestDto(title = title, description = description)

fun MedicationDto.toDomain(): Medication? {
    val resolvedId = id.asIntOrNull() ?: return null
    val patient = patientId.asIntOrNull() ?: 0
    return Medication(
        id = resolvedId,
        name = name.orEmpty(),
        description = description.orEmpty(),
        patientId = patient,
        interval = interval.orEmpty(),
        quantity = quantity.orEmpty()
    )
}

fun MedicationRequest.toDto(): MedicationRequestDto = MedicationRequestDto(
    name = name,
    description = description,
    patientId = patientId,
    interval = interval,
    quantity = quantity
)

fun MedicationUpdateRequest.toDto(): MedicationUpdateRequestDto = MedicationUpdateRequestDto(
    name = name,
    description = description,
    interval = interval,
    quantity = quantity
)

fun PatientProfileRequest.toDto(): PatientProfileRequestDto = PatientProfileRequestDto(
    firstName = firstName,
    lastName = lastName,
    street = street,
    city = city,
    country = country,
    email = email,
    username = username,
    password = password,
    professionalId = professionalId
)

fun UpdatePatientProfileRequest.toDto(): UpdatePatientProfileRequestDto = UpdatePatientProfileRequestDto(
    firstName = firstName,
    lastName = lastName,
    street = street,
    city = city,
    country = country,
    email = email
)

fun ProfessionalProfileRequest.toDto(): ProfessionalProfileRequestDto = ProfessionalProfileRequestDto(
    firstName = firstName,
    lastName = lastName,
    street = street,
    city = city,
    country = country,
    email = email,
    username = username,
    password = password
)

fun MoodStateDto.toDomain(): MoodState? {
    val resolvedId = id.asIntOrNull() ?: return null
    val statusValue = status.asIntOrNull() ?: 2
    val mood = statusValue + 1 // convert backend 0-4 to 1-5
    val patient = idPatient.asIntOrNull()
    val recorded = parseLocalDate(createdAt) ?: parseLocalDate(recordDate) ?: parseLocalDate(date)
    return MoodState(
        id = resolvedId,
        patientId = patient,
        mood = mood,
        recordedAt = recorded
    )
}

fun BiologicalFunctionDto.toDomain(): BiologicalFunctions? {
    val resolvedId = id.asIntOrNull() ?: return null
    val patient = idPatient.asIntOrNull() ?: 0
    val recorded = parseLocalDate(createdAt) ?: parseLocalDate(recordDate) ?: parseLocalDate(date)
    return BiologicalFunctions(
        id = resolvedId,
        hunger = (hunger.asIntOrNull() ?: 0).coerceIn(0, 10),
        hydration = (hydration.asIntOrNull() ?: 0).coerceIn(0, 10),
        sleep = (sleep.asIntOrNull() ?: 0).coerceIn(0, 10),
        energy = (energy.asIntOrNull() ?: 0).coerceIn(0, 10),
        patientId = patient,
        recordedAt = recorded
    )
}

fun List<MoodState>.toAnalytic(year: String, month: String, patientId: Int): MoodAnalytic {
    val filtered = filter { moodState ->
        moodState.recordedAt?.let {
            it.year.toString() == year && it.monthValue.toString() == month
        } ?: false
    }
    var soSad = 0
    var sad = 0
    var neutral = 0
    var happy = 0
    var soHappy = 0
    filtered.forEach { state ->
        when (state.mood) {
            1 -> soSad++
            2 -> sad++
            3 -> neutral++
            4 -> happy++
            5 -> soHappy++
        }
    }
    return MoodAnalytic(
        patientId = patientId.toString(),
        year = year,
        month = month,
        soSadMood = soSad,
        sadMood = sad,
        neutralMood = neutral,
        happyMood = happy,
        soHappyMood = soHappy
    )
}

fun List<BiologicalFunctions>.toAnalytic(year: String, month: String, patientId: Int): BiologicalAnalytic {
    val filtered = filter { entry ->
        entry.recordedAt?.let {
            it.year.toString() == year && it.monthValue.toString() == month
        } ?: false
    }
    if (filtered.isEmpty()) {
        return BiologicalAnalytic(
            patientId = patientId.toString(),
            month = month,
            year = year,
            hungerAverage = 0.0,
            hydrationAverage = 0.0,
            sleepAverage = 0.0,
            energyAverage = 0.0
        )
    }
    val hungerAverage = filtered.map { it.hunger }.average()
    val hydrationAverage = filtered.map { it.hydration }.average()
    val sleepAverage = filtered.map { it.sleep }.average()
    val energyAverage = filtered.map { it.energy }.average()

    return BiologicalAnalytic(
        patientId = patientId.toString(),
        month = month,
        year = year,
        hungerAverage = hungerAverage,
        hydrationAverage = hydrationAverage,
        sleepAverage = sleepAverage,
        energyAverage = energyAverage
    )
}

