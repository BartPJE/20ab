package com.twentyab.tracker.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twentyab.tracker.data.model.NewGameParticipant
import com.twentyab.tracker.data.model.SessionDetail
import com.twentyab.tracker.data.model.TrumpSuit
import com.twentyab.tracker.data.repository.TwentyAbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewGameViewModel(
    private val repository: TwentyAbRepository,
    private val sessionId: Long
) : ViewModel() {
    val session: StateFlow<SessionDetail?> = repository.observeSessionDetail(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun createGame(
        callerId: Long,
        trumpSuit: TrumpSuit,
        heartBlind: Boolean,
        participants: List<NewGameParticipant>,
        onResult: (Result<Long>) -> Unit
    ) {
        if (_isSaving.value) return
        _isSaving.value = true
        viewModelScope.launch {
            runCatching {
                repository.createGame(sessionId, callerId, trumpSuit, heartBlind, participants)
            }.onSuccess { gameId ->
                onResult(Result.success(gameId))
            }.onFailure { throwable ->
                _errorMessage.value = throwable.localizedMessage
                onResult(Result.failure(throwable))
            }
            _isSaving.value = false
        }
    }
}
