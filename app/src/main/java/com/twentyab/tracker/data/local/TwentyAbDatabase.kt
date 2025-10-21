package com.twentyab.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        PlayerEntity::class,
        SessionEntity::class,
        SessionPlayerCrossRef::class,
        GameEntity::class,
        GameParticipantEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TwentyAbDatabase : RoomDatabase() {
    abstract fun dao(): TwentyAbDao
}
