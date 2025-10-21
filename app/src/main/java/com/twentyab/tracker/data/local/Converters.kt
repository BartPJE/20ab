package com.twentyab.tracker.data.local

import androidx.room.TypeConverter
import com.twentyab.tracker.data.model.TrumpSuit
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun localDateToLong(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromTrumpSuit(name: String?): TrumpSuit? = name?.let { TrumpSuit.valueOf(it) }

    @TypeConverter
    fun trumpSuitToName(trumpSuit: TrumpSuit?): String? = trumpSuit?.name
}
