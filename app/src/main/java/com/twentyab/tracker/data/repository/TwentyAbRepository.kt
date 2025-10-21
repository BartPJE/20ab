package com.twentyab.tracker.data.repository

import com.twentyab.tracker.data.local.GameParticipantEntity
import com.twentyab.tracker.data.local.GameWithParticipants
import com.twentyab.tracker.data.local.PlayerEntity
import com.twentyab.tracker.data.local.SessionPlayerCrossRef
import com.twentyab.tracker.data.local.SessionWithPlayers
import com.twentyab.tracker.data.local.TwentyAbDao
import com.twentyab.tracker.data.local.SessionEntity
import com.twentyab.tracker.data.local.GameEntity
import com.twentyab.tracker.data.model.GameDetail
import com.twentyab.tracker.data.model.GameParticipantDetail
import com.twentyab.tracker.data.model.NewGameParticipant
import com.twentyab.tracker.data.model.NewSessionPlayer
import com.twentyab.tracker.data.model.PlayerReference
import com.twentyab.tracker.data.model.PlayerSeat
import com.twentyab.tracker.data.model.PlayerSessionStats
import com.twentyab.tracker.data.model.SessionDetail
import com.twentyab.tracker.data.model.SessionSummary
import com.twentyab.tracker.data.model.StatisticsOverview
import com.twentyab.tracker.data.model.TrumpSuit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TwentyAbRepository(private val dao: TwentyAbDao) {

    fun observeSessionSummaries(): Flow<List<SessionSummary>> =
        combine(dao.observeSessions(), dao.observeAllGames()) { sessions, games ->
            val gamesBySession = games.groupBy { it.game.sessionId }
            sessions.map { sessionWithPlayers ->
                sessionWithPlayers.toSummary(gamesBySession[sessionWithPlayers.session.id])
            }
        }

    fun observeSessionDetail(sessionId: Long): Flow<SessionDetail?> =
        combine(
            dao.observeSession(sessionId),
            dao.observeGamesForSession(sessionId)
        ) { session, games ->
            session?.toDetail(games)
        }

    fun observeStatistics(): Flow<StatisticsOverview> =
        combine(dao.observeAllGames(), dao.observePlayers()) { games, players ->
            buildStatistics(games, players)
        }

    suspend fun createSession(date: LocalDate, players: List<NewSessionPlayer>): Long =
        withContext(Dispatchers.IO) {
            val trimmedPlayers = players.mapNotNull { player ->
                val name = player.name.trim()
                if (name.isEmpty()) null else player.copy(name = name)
            }
            require(trimmedPlayers.size == players.size) {
                "Alle Spielernamen müssen ausgefüllt sein."
            }

            val playerIds = trimmedPlayers.map { upsertPlayer(it.name) }
            val sessionId = dao.insertSession(SessionEntity(date = date))
            val crossRefs = trimmedPlayers.zip(playerIds) { player, id ->
                SessionPlayerCrossRef(
                    sessionId = sessionId,
                    playerId = id,
                    seatIndex = player.seatIndex
                )
            }
            dao.insertSessionPlayers(crossRefs)
            sessionId
        }

    suspend fun createGame(
        sessionId: Long,
        callerId: Long,
        trumpSuit: TrumpSuit,
        heartBlind: Boolean,
        participants: List<NewGameParticipant>
    ): Long = withContext(Dispatchers.IO) {
        require(participants.isNotEmpty()) { "Teilnehmerliste darf nicht leer sein." }
        val gameId = dao.insertGame(
            GameEntity(
                sessionId = sessionId,
                callerId = callerId,
                trumpSuit = trumpSuit,
                heartBlind = heartBlind
            )
        )
        val participantEntities = participants.map {
            GameParticipantEntity(
                gameId = gameId,
                playerId = it.playerId,
                isPlaying = it.isPlaying,
                tricksWon = it.tricksWon,
                amountPaid = it.amountPaid
            )
        }
        dao.insertParticipants(participantEntities)
        gameId
    }

    private suspend fun upsertPlayer(name: String): Long {
        val existing = dao.getPlayerByName(name)
        if (existing != null) {
            return existing.id
        }
        val insertedId = dao.insertPlayer(PlayerEntity(name = name))
        if (insertedId != -1L) {
            return insertedId
        }
        // Fallback in case of race condition
        return dao.getPlayerByName(name)?.id
            ?: error("Spieler konnte nicht gespeichert werden")
    }

    private fun SessionWithPlayers.toSummary(games: List<GameWithParticipants>?): SessionSummary {
        val orderedSeats = players
            .sortedBy { it.crossRef.seatIndex }
            .map {
                PlayerSeat(
                    playerId = it.player.id,
                    name = it.player.name,
                    seatIndex = it.crossRef.seatIndex
                )
            }
        return SessionSummary(
            id = session.id,
            date = session.date,
            seatOrder = orderedSeats,
            gameCount = games?.size ?: 0
        )
    }

    private fun SessionWithPlayers.toDetail(games: List<GameWithParticipants>): SessionDetail {
        val playerStats = players
            .sortedBy { it.crossRef.seatIndex }
            .map { sessionPlayer ->
                val player = sessionPlayer.player
                val playerGames = games.mapNotNull { game ->
                    game.participants.firstOrNull { it.player.id == player.id }
                }
                val tricksWon = playerGames.sumOf { it.participant.tricksWon ?: 0 }
                val gamesPlayed = playerGames.count { it.participant.isPlaying }
                val gamesSkipped = playerGames.count { !it.participant.isPlaying }
                val amountPaid = playerGames.sumOf { it.participant.amountPaid }
                PlayerSessionStats(
                    playerId = player.id,
                    name = player.name,
                    seatIndex = sessionPlayer.crossRef.seatIndex,
                    tricksWon = tricksWon,
                    gamesPlayed = gamesPlayed,
                    gamesSkipped = gamesSkipped,
                    amountPaid = amountPaid
                )
            }

        val mappedGames = games.map { it.toDetail(session.session.id) }

        return SessionDetail(
            id = session.id,
            date = session.date,
            players = playerStats,
            games = mappedGames
        )
    }

    private fun GameWithParticipants.toDetail(sessionId: Long): GameDetail {
        val participants = participants.map {
            GameParticipantDetail(
                player = PlayerReference(id = it.player.id, name = it.player.name),
                isPlaying = it.participant.isPlaying,
                tricksWon = it.participant.tricksWon,
                amountPaid = it.participant.amountPaid
            )
        }
        val caller = participants.first { it.player.id == game.callerId }.player
        return GameDetail(
            id = game.id,
            sessionId = sessionId,
            trumpSuit = game.trumpSuit,
            caller = caller,
            heartBlind = game.heartBlind,
            createdAt = game.createdAt,
            participants = participants
        )
    }

    private fun buildStatistics(
        games: List<GameWithParticipants>,
        players: List<PlayerEntity>
    ): StatisticsOverview {
        val playerRefs = players.associateBy({ it.id }) { PlayerReference(it.id, it.name) }
        val calledTrump = mutableMapOf<PlayerReference, MutableMap<TrumpSuit, Int>>()
        val heartBlindCalls = mutableMapOf<PlayerReference, Int>()
        val skipped = mutableMapOf<PlayerReference, Int>()
        val payments = mutableMapOf<PlayerReference, Int>()
        val tricks = mutableMapOf<PlayerReference, Int>()

        games.forEach { gameWithParticipants ->
            val callerRef = playerRefs[gameWithParticipants.game.callerId] ?: return@forEach
            val suitMap = calledTrump.getOrPut(callerRef) { mutableMapOf() }
            suitMap[gameWithParticipants.game.trumpSuit] =
                suitMap.getOrDefault(gameWithParticipants.game.trumpSuit, 0) + 1
            if (gameWithParticipants.game.heartBlind && gameWithParticipants.game.trumpSuit == TrumpSuit.HERZ) {
                heartBlindCalls[callerRef] = heartBlindCalls.getOrDefault(callerRef, 0) + 1
            }
            gameWithParticipants.participants.forEach { participant ->
                val ref = playerRefs[participant.player.id] ?: return@forEach
                if (participant.participant.isPlaying) {
                    tricks[ref] = tricks.getOrDefault(ref, 0) + (participant.participant.tricksWon ?: 0)
                } else {
                    skipped[ref] = skipped.getOrDefault(ref, 0) + 1
                }
                payments[ref] = payments.getOrDefault(ref, 0) + participant.participant.amountPaid
            }
        }

        val ensureEntries = { map: MutableMap<PlayerReference, Int> ->
            players.forEach { player ->
                val ref = playerRefs.getValue(player.id)
                map.putIfAbsent(ref, 0)
            }
        }
        ensureEntries(heartBlindCalls)
        ensureEntries(skipped)
        ensureEntries(payments)
        ensureEntries(tricks)

        val calledTrumpCompleted = calledTrump.mapValues { entry ->
            TrumpSuit.entries.associateWith { suit -> entry.value.getOrDefault(suit, 0) }
        }.toMutableMap()

        players.forEach { player ->
            val ref = playerRefs.getValue(player.id)
            calledTrumpCompleted.putIfAbsent(ref, TrumpSuit.entries.associateWith { 0 })
        }

        return StatisticsOverview(
            calledTrumpCounts = calledTrumpCompleted,
            heartBlindCalls = heartBlindCalls,
            skippedGames = skipped,
            totalPayments = payments,
            totalTricks = tricks
        )
    }
}
