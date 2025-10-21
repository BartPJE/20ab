package com.twentyab.tracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twentyab.tracker.data.model.PlayerReference
import com.twentyab.tracker.data.model.StatisticsOverview
import com.twentyab.tracker.data.model.TrumpSuit
import com.twentyab.tracker.data.repository.TwentyAbRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(repository: TwentyAbRepository) : ViewModel() {
    private val emptyStatistics = StatisticsOverview(
        calledTrumpCounts = emptyMap<PlayerReference, Map<TrumpSuit, Int>>(),
        heartBlindCalls = emptyMap(),
        skippedGames = emptyMap(),
        totalPayments = emptyMap(),
        totalTricks = emptyMap()
    )

    val statistics: StateFlow<StatisticsOverview> = repository.observeStatistics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyStatistics
        )
}
