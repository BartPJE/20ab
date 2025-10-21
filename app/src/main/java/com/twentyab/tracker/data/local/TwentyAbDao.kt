package com.twentyab.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TwentyAbDao {
    @Transaction
    @Query("SELECT * FROM sessions ORDER BY date DESC, id DESC")
    fun observeSessions(): Flow<List<SessionWithPlayers>>

    @Transaction
    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    fun observeSession(sessionId: Long): Flow<SessionWithPlayers?>

    @Transaction
    @Query("SELECT * FROM games WHERE sessionId = :sessionId ORDER BY createdAt DESC, id DESC")
    fun observeGamesForSession(sessionId: Long): Flow<List<GameWithParticipants>>

    @Transaction
    @Query("SELECT * FROM games ORDER BY createdAt DESC, id DESC")
    fun observeAllGames(): Flow<List<GameWithParticipants>>

    @Query("SELECT * FROM players ORDER BY name ASC")
    fun observePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun getPlayerByName(name: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayer(playerEntity: PlayerEntity): Long

    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Insert
    suspend fun insertSessionPlayers(players: List<SessionPlayerCrossRef>)

    @Insert
    suspend fun insertGame(game: GameEntity): Long

    @Insert
    suspend fun insertParticipants(participants: List<GameParticipantEntity>)
}
