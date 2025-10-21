package com.twentyab.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twentyab.tracker.data.repository.TwentyAbRepository
import com.twentyab.tracker.ui.game.NewGameViewModel
import com.twentyab.tracker.ui.sessions.NewSessionViewModel
import com.twentyab.tracker.ui.sessions.SessionDetailViewModel
import com.twentyab.tracker.ui.sessions.SessionsViewModel
import com.twentyab.tracker.ui.statistics.StatisticsViewModel

@Suppress("UNCHECKED_CAST")
class TwentyAbViewModelFactory(
    private val repository: TwentyAbRepository,
    private val sessionId: Long? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SessionsViewModel::class.java) ->
                SessionsViewModel(repository) as T
            modelClass.isAssignableFrom(NewSessionViewModel::class.java) ->
                NewSessionViewModel(repository) as T
            modelClass.isAssignableFrom(SessionDetailViewModel::class.java) && sessionId != null ->
                SessionDetailViewModel(repository, sessionId) as T
            modelClass.isAssignableFrom(NewGameViewModel::class.java) && sessionId != null ->
                NewGameViewModel(repository, sessionId) as T
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) ->
                StatisticsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        }
    }
}
