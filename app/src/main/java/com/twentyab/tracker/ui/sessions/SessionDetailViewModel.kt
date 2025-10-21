package com.twentyab.tracker.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twentyab.tracker.data.model.SessionDetail
import com.twentyab.tracker.data.repository.TwentyAbRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionDetailViewModel(
    repository: TwentyAbRepository,
    sessionId: Long
) : ViewModel() {
    val session: StateFlow<SessionDetail?> = repository.observeSessionDetail(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
