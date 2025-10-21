package com.twentyab.tracker.data.repository

import android.content.Context
import androidx.room.Room
import com.twentyab.tracker.data.local.TwentyAbDatabase

class AppContainer(context: Context) {
    private val database: TwentyAbDatabase = Room.databaseBuilder(
        context,
        TwentyAbDatabase::class.java,
        "twentyab-database"
    ).fallbackToDestructiveMigration().build()

    val repository: TwentyAbRepository = TwentyAbRepository(database.dao())
}
