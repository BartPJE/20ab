package com.twentyab.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.twentyab.tracker.data.repository.TwentyAbRepository
import com.twentyab.tracker.ui.TwentyAbApp

class MainActivity : ComponentActivity() {
    private val repository: TwentyAbRepository by lazy {
        (application as TwentyAbTrackerApp).container.repository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwentyAbTheme {
                TwentyAbApp(repository = repository)
            }
        }
    }
}
