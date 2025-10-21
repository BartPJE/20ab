package com.twentyab.tracker.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twentyab.tracker.data.model.SessionSummary
import com.twentyab.tracker.data.repository.TwentyAbRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionsViewModel(repository: TwentyAbRepository) : ViewModel() {
    val sessions: StateFlow<List<SessionSummary>> = repository.observeSessionSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
