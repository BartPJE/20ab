package com.twentyab.tracker

import android.app.Application
import com.twentyab.tracker.data.repository.AppContainer

class TwentyAbTrackerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
