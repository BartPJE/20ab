package com.twentyab.tracker.data.local

import androidx.room.Entity

@Entity(
    tableName = "session_players",
    primaryKeys = ["sessionId", "playerId"]
)
data class SessionPlayerCrossRef(
    val sessionId: Long,
    val playerId: Long,
    val seatIndex: Int
)
