package com.twentyab.tracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.twentyab.tracker.data.model.TrumpSuit
import java.time.Instant

@Entity(
    tableName = "games",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["callerId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["sessionId"]), Index(value = ["callerId"])]
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val callerId: Long,
    val trumpSuit: TrumpSuit,
    val heartBlind: Boolean,
    val createdAt: Instant = Instant.now()
)
