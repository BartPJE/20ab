package com.twentyab.tracker.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithParticipants(
    @Embedded val game: GameEntity,
    @Relation(
        entity = GameParticipantEntity::class,
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val participants: List<GameParticipant>
)

data class GameParticipant(
    @Embedded val participant: GameParticipantEntity,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: PlayerEntity
)
