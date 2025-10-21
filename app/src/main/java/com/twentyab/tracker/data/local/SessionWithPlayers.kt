package com.twentyab.tracker.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithPlayers(
    @Embedded val session: SessionEntity,
    @Relation(
        entity = SessionPlayerCrossRef::class,
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val players: List<SessionPlayer>
)

data class SessionPlayer(
    @Embedded val crossRef: SessionPlayerCrossRef,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: PlayerEntity
)
