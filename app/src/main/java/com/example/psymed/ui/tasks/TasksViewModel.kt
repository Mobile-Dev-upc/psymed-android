package com.example.psymed.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psymed.data.repository.TaskRepository
import com.example.psymed.domain.model.Task
import com.example.psymed.domain.model.TaskRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
) {
    val completedTasks: List<Task> get() = tasks.filter { it.isCompleted }
    val pendingTasks: List<Task> get() = tasks.filter { !it.isCompleted }
    val completionRate: Double
        get() = if (tasks.isEmpty()) 0.0 else (completedTasks.size.toDouble() / tasks.size) * 100
}

class TasksViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun loadTasksByPatient(patientId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.getTasksByPatient(patientId)
            }.onSuccess { tasks ->
                _uiState.update { it.copy(isLoading = false, tasks = tasks, error = null) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = emptyList(),
                        error = throwable.message ?: "Failed to load tasks"
                    )
                }
            }
        }
    }

    fun createTask(sessionId: Int, request: TaskRequest, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.createTask(sessionId, request)
            }.onSuccess { task ->
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tasks = (it.tasks + task).distinctBy { item -> item.id }
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to create task")
            }
        }
    }

    fun updateTask(sessionId: Int, taskId: Int, request: TaskRequest, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.updateTask(sessionId, taskId, request)
            }.onSuccess { task ->
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tasks = it.tasks.map { current -> if (current.id == task.id) task else current }
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to update task")
            }
        }
    }

    fun deleteTask(sessionId: Int, taskId: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.deleteTask(sessionId, taskId)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = it.tasks.filterNot { task -> task.id.toIntOrNull() == taskId || task.id == taskId.toString() }
                    )
                }
                onResult(true, null)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, throwable.message ?: "Failed to delete task")
            }
        }
    }

    fun toggleTaskStatus(sessionId: Int, task: Task, onResult: (Boolean, String?) -> Unit) {
        val taskId = task.id.toIntOrNull() ?: return
        viewModelScope.launch {
            runCatching {
                if (task.isCompleted) {
                    repository.markTaskIncomplete(sessionId, taskId)
                } else {
                    repository.markTaskComplete(sessionId, taskId)
                }
            }.onSuccess {
                _uiState.update {
                    val updated = it.tasks.map { current ->
                        if (current.id == task.id) current.copy(status = if (task.isCompleted) 0 else 1) else current
                    }
                    it.copy(tasks = updated)
                }
                onResult(true, null)
            }.onFailure { throwable ->
                onResult(false, throwable.message ?: "Failed to update task status")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

