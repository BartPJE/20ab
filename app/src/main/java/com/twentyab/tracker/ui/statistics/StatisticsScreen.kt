package com.twentyab.tracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twentyab.tracker.data.model.PlayerReference
import com.twentyab.tracker.data.model.StatisticsOverview
import com.twentyab.tracker.data.model.TrumpSuit

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val statistics by viewModel.statistics.collectAsState()
    StatisticsContent(statistics = statistics, modifier = modifier)
}

@Composable
private fun StatisticsContent(
    statistics: StatisticsOverview,
    modifier: Modifier = Modifier
) {
    val playerEntries = statistics.calledTrumpCounts.entries.sortedBy { it.key.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Statistik",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        items(playerEntries) { entry ->
            PlayerStatisticsCard(
                player = entry.key,
                suits = entry.value,
                heartBlind = statistics.heartBlindCalls[entry.key] ?: 0,
                skipped = statistics.skippedGames[entry.key] ?: 0,
                payments = statistics.totalPayments[entry.key] ?: 0,
                tricks = statistics.totalTricks[entry.key] ?: 0
            )
        }
        if (statistics.calledTrumpCounts.isEmpty()) {
            item {
                Text(text = "Noch keine Daten vorhanden.")
            }
        }
    }
}

@Composable
private fun PlayerStatisticsCard(
    player: PlayerReference,
    suits: Map<TrumpSuit, Int>,
    heartBlind: Int,
    skipped: Int,
    payments: Int,
    tricks: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Trumpfansagen:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            TrumpSuit.entries.forEach { suit ->
                Text(text = "- ${suit.displayName}: ${suits[suit] ?: 0}")
            }
            Text(
                text = "Herz Blind: $heartBlind",
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(text = "Ausgesetzt: $skipped")
            Text(text = "Gezahlt: $payments")
            Text(text = "Gewonnene Stiche: $tricks")
        }
    }
}
