package com.twentyab.tracker.data.model

import java.time.Instant
import java.time.LocalDate

data class SessionSummary(
    val id: Long,
    val date: LocalDate,
    val seatOrder: List<PlayerSeat>,
    val gameCount: Int
)

data class PlayerSeat(
    val playerId: Long,
    val name: String,
    val seatIndex: Int
)

data class SessionDetail(
    val id: Long,
    val date: LocalDate,
    val players: List<PlayerSessionStats>,
    val games: List<GameDetail>
)

data class PlayerSessionStats(
    val playerId: Long,
    val name: String,
    val seatIndex: Int,
    val tricksWon: Int,
    val gamesPlayed: Int,
    val gamesSkipped: Int,
    val amountPaid: Int
) {
    val remainingPoints: Int get() = 20 - tricksWon
}

data class GameDetail(
    val id: Long,
    val sessionId: Long,
    val trumpSuit: TrumpSuit,
    val caller: PlayerReference,
    val heartBlind: Boolean,
    val createdAt: Instant,
    val participants: List<GameParticipantDetail>
) {
    val multiplier: Int
        get() = when {
            heartBlind && trumpSuit == TrumpSuit.HERZ -> 4
            trumpSuit == TrumpSuit.HERZ -> 2
            else -> 1
        }
}

data class GameParticipantDetail(
    val player: PlayerReference,
    val isPlaying: Boolean,
    val tricksWon: Int?,
    val amountPaid: Int
)

data class PlayerReference(
    val id: Long,
    val name: String
)

data class StatisticsOverview(
    val calledTrumpCounts: Map<PlayerReference, Map<TrumpSuit, Int>>,
    val heartBlindCalls: Map<PlayerReference, Int>,
    val skippedGames: Map<PlayerReference, Int>,
    val totalPayments: Map<PlayerReference, Int>,
    val totalTricks: Map<PlayerReference, Int>
)
