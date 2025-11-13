package com.example.psymed.data

import com.example.psymed.data.repository.RepositoryContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ServiceLocator {
    private val _tokenState = MutableStateFlow<String?>(null)
    val tokenState: StateFlow<String?> = _tokenState

    val repositories = RepositoryContainer { _tokenState.value }

    fun updateToken(token: String?) {
        _tokenState.value = token
    }
}

