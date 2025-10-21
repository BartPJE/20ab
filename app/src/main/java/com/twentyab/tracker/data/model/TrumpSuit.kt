package com.twentyab.tracker.data.model

enum class TrumpSuit(val displayName: String) {
    HERZ("Herz"),
    EICHEL("Eichel"),
    SCHELL("Schell"),
    BLATT("Blatt");

    companion object {
        fun fromDisplayName(displayName: String): TrumpSuit? = entries.firstOrNull {
            it.displayName.equals(displayName, ignoreCase = true)
        }
    }
}
