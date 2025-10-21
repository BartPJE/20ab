package com.twentyab.tracker.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twentyab.tracker.data.model.NewGameParticipant
import com.twentyab.tracker.data.model.TrumpSuit

@Composable
fun NewGameScreen(
    viewModel: NewGameViewModel,
    onGameCreated: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.session.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    session?.let { detail ->
        var selectedTrump by remember { mutableStateOf(TrumpSuit.HERZ) }
        var heartBlind by remember { mutableStateOf(false) }
        var callerId by remember { mutableStateOf(detail.players.firstOrNull()?.playerId) }
        val participantStates = remember(detail) {
            mutableStateMapOf<Long, ParticipantInputState>().apply {
                detail.players.forEach { player ->
                    this[player.playerId] = ParticipantInputState(isPlaying = true)
                }
            }
        }

        if (selectedTrump != TrumpSuit.HERZ && heartBlind) {
            heartBlind = false
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Neues Spiel",
                style = MaterialTheme.typography.headlineSmall
            )
            TrumpSelector(
                selected = selectedTrump,
                onSelected = { suit ->
                    selectedTrump = suit
                    if (suit != TrumpSuit.HERZ) {
                        heartBlind = false
                    }
                    if (suit == TrumpSuit.EICHEL) {
                        detail.players.forEach { player ->
                            participantStates[player.playerId]?.isPlaying = true
                        }
                    }
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = heartBlind,
                    onCheckedChange = {
                        if (selectedTrump == TrumpSuit.HERZ) {
                            heartBlind = it
                        }
                    }
                )
                Text(text = "Herz Blind (4x)")
            }
            Text(text = "Ausrufer")
            CallerSelector(
                options = detail.players.map { it.playerId to it.name },
                selected = callerId,
                onSelected = { callerId = it }
            )
            Text(text = "Teilnehmer")
            detail.players.forEach { player ->
                val state = participantStates.getValue(player.playerId)
                ParticipantEditor(
                    name = player.name,
                    state = state,
                    disableSkip = selectedTrump == TrumpSuit.EICHEL
                )
            }
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
            Button(
                onClick = {
                    val participants = participantStates.map { (id, state) ->
                        val playing = state.isPlaying || selectedTrump == TrumpSuit.EICHEL
                        NewGameParticipant(
                            playerId = id,
                            isPlaying = playing,
                            tricksWon = if (playing) state.tricks.toIntOrNull() ?: 0 else null,
                            amountPaid = state.amount.toIntOrNull() ?: 0
                        )
                    }
                    val caller = callerId
                    if (caller != null) {
                        viewModel.clearError()
                        viewModel.createGame(
                            callerId = caller,
                            trumpSuit = selectedTrump,
                            heartBlind = heartBlind,
                            participants = participants
                        ) { result ->
                            result.onSuccess { onGameCreated() }
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isSaving) "Speichere..." else "Speichern")
            }
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Abbrechen")
            }
        }
    } ?: Text(text = "Lade...", modifier = Modifier.padding(16.dp))
}

@Composable
private fun TrumpSelector(
    selected: TrumpSuit,
    onSelected: (TrumpSuit) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Trumpffarbe")
        TrumpSuit.entries.forEach { suit ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = suit == selected,
                    onCheckedChange = { if (it) onSelected(suit) }
                )
                Text(text = suit.displayName)
            }
        }
    }
}

@Composable
private fun CallerSelector(
    options: List<Pair<Long, String>>,
    selected: Long?,
    onSelected: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { (id, name) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = id == selected,
                    onCheckedChange = { if (it) onSelected(id) }
                )
                Text(text = name)
            }
        }
    }
}

@Composable
private fun ParticipantEditor(
    name: String,
    state: ParticipantInputState,
    disableSkip: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        if (disableSkip && !state.isPlaying) {
            state.isPlaying = true
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.isPlaying || disableSkip,
                onCheckedChange = { state.isPlaying = it },
                enabled = !disableSkip
            )
            Text(text = name, style = MaterialTheme.typography.titleMedium)
        }
        if (state.isPlaying || disableSkip) {
            OutlinedTextField(
                value = state.tricks,
                onValueChange = { value -> if (value.all { it.isDigit() }) state.tricks = value },
                label = { Text(text = "Stiche") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            value = state.amount,
            onValueChange = { value -> if (value.all { it.isDigit() }) state.amount = value },
            label = { Text(text = "Einsatz") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private class ParticipantInputState(
    isPlaying: Boolean,
    tricks: String = "0",
    amount: String = "0"
) {
    var isPlaying by mutableStateOf(isPlaying)
    var tricks by mutableStateOf(tricks)
    var amount by mutableStateOf(amount)
}
