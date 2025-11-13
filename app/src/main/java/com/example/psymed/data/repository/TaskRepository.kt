package com.example.psymed.data.repository

import com.example.psymed.data.api.PsyMedApi
import com.example.psymed.data.model.toDomain
import com.example.psymed.data.model.toDto
import com.example.psymed.domain.model.Task
import com.example.psymed.domain.model.TaskRequest

class TaskRepository(
    private val api: PsyMedApi
) {

    suspend fun getTasksByPatient(patientId: Int): List<Task> =
        api.getTasksByPatient(patientId).mapNotNull { it.toDomain() }

    suspend fun getTasksBySession(sessionId: Int): List<Task> =
        api.getTasksBySession(sessionId).mapNotNull { it.toDomain() }

    suspend fun createTask(sessionId: Int, request: TaskRequest): Task? =
        api.createTask(sessionId, request.toDto()).toDomain()

    suspend fun updateTask(sessionId: Int, taskId: Int, request: TaskRequest): Task? =
        api.updateTask(sessionId, taskId, request.toDto()).toDomain()

    suspend fun deleteTask(sessionId: Int, taskId: Int) {
        api.deleteTask(sessionId, taskId)
    }

    suspend fun markTaskComplete(sessionId: Int, taskId: Int) {
        api.markTaskComplete(sessionId, taskId)
    }

    suspend fun markTaskIncomplete(sessionId: Int, taskId: Int) {
        api.markTaskIncomplete(sessionId, taskId)
    }
}

