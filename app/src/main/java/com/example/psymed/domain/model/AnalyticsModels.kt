package com.example.psymed.domain.model

import java.time.LocalDate

data class MoodState(
    val id: Int,
    val patientId: Int?,
    val mood: Int,
    val recordedAt: LocalDate?
) {
    fun getMoodLabel(): String = when (mood) {
        1 -> "So Sad"
        2 -> "Sad"
        3 -> "Neutral"
        4 -> "Happy"
        5 -> "So Happy"
        else -> "Neutral"
    }

    fun getMoodEmoji(): String = when (mood) {
        1 -> "😢"
        2 -> "😕"
        3 -> "😐"
        4 -> "😊"
        5 -> "😄"
        else -> "😐"
    }
}

data class BiologicalFunctions(
    val id: Int,
    val hunger: Int,
    val hydration: Int,
    val sleep: Int,
    val energy: Int,
    val patientId: Int,
    val recordedAt: LocalDate?
)

data class MoodAnalytic(
    val patientId: String,
    val year: String,
    val month: String,
    val soSadMood: Int,
    val sadMood: Int,
    val neutralMood: Int,
    val happyMood: Int,
    val soHappyMood: Int
) {
    val totalMoods: Int
        get() = soSadMood + sadMood + neutralMood + happyMood + soHappyMood
}

data class BiologicalAnalytic(
    val patientId: String,
    val month: String,
    val year: String,
    val hungerAverage: Double,
    val hydrationAverage: Double,
    val sleepAverage: Double,
    val energyAverage: Double
)

