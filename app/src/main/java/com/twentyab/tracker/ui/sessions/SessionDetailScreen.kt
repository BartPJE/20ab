package com.twentyab.tracker.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twentyab.tracker.data.model.GameDetail
import com.twentyab.tracker.data.model.GameParticipantDetail
import com.twentyab.tracker.data.model.SessionDetail
import com.twentyab.tracker.util.Formatters

@Composable
fun SessionDetailScreen(
    viewModel: SessionDetailViewModel,
    onAddGame: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.session.collectAsState()
    session?.let {
        SessionDetailContent(
            detail = it,
            onAddGame = { onAddGame(it.id) },
            modifier = modifier
        )
    } ?: Text(
        text = "Lade...",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun SessionDetailContent(
    detail: SessionDetail,
    onAddGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = Formatters.formatDate(detail.date),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Sitzreihenfolge",
            style = MaterialTheme.typography.titleMedium
        )
        detail.players.sortedBy { it.seatIndex }.forEach { player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${player.seatIndex + 1}. ${player.name}")
                Text(text = "Punkte: ${player.remainingPoints}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 12.dp))
        Text(
            text = "Spiele (${detail.games.size})",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = onAddGame,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .align(Alignment.End)
        ) {
            Text(text = "Neues Spiel")
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxWidth()
        ) {
            items(detail.games) { game ->
                GameCard(game = game)
            }
        }
    }
}

@Composable
private fun GameCard(game: GameDetail) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trumpf: ${game.trumpSuit.displayName}${if (game.heartBlind) " (Herz Blind)" else ""}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Ausgerufen von: ${game.caller.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Multiplikator: ${game.multiplier}x",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Datum: ${Formatters.formatInstant(game.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            game.participants.forEach { participant ->
                ParticipantRow(participant)
            }
        }
    }
}

@Composable
private fun ParticipantRow(participant: GameParticipantDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = participant.player.name)
        val statusText = if (participant.isPlaying) {
            "Stiche: ${participant.tricksWon ?: 0} | Einsatz: ${participant.amountPaid}"
        } else {
            "Ausgesetzt"
        }
        Text(text = statusText)
    }
}
