package com.twentyab.tracker.data.model

data class NewSessionPlayer(val name: String, val seatIndex: Int)

data class NewGameParticipant(
    val playerId: Long,
    val isPlaying: Boolean,
    val tricksWon: Int?,
    val amountPaid: Int
)
