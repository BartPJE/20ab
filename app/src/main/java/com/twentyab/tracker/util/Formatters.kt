package com.twentyab.tracker.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    fun formatDate(date: LocalDate): String = dateFormatter.format(date)

    fun formatInstant(instant: Instant): String {
        val zoned = instant.atZone(ZoneId.systemDefault())
        return dateTimeFormatter.format(zoned)
    }
}
