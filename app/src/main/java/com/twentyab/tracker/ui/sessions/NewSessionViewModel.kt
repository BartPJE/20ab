package com.twentyab.tracker.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twentyab.tracker.data.model.NewSessionPlayer
import com.twentyab.tracker.data.repository.TwentyAbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class NewSessionViewModel(private val repository: TwentyAbRepository) : ViewModel() {
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun createSession(
        date: LocalDate,
        playerNames: List<String>,
        onResult: (Result<Long>) -> Unit
    ) {
        if (_isSaving.value) return
        _isSaving.value = true
        viewModelScope.launch {
            runCatching {
                val players = playerNames.mapIndexed { index, name ->
                    NewSessionPlayer(name = name, seatIndex = index)
                }
                repository.createSession(date, players)
            }.onSuccess { sessionId ->
                onResult(Result.success(sessionId))
            }.onFailure { throwable ->
                _errorMessage.value = throwable.localizedMessage
                onResult(Result.failure(throwable))
            }
            _isSaving.value = false
        }
    }
}
