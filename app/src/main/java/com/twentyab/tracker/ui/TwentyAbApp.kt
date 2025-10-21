package com.twentyab.tracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.twentyab.tracker.data.repository.TwentyAbRepository
import com.twentyab.tracker.ui.game.NewGameScreen
import com.twentyab.tracker.ui.game.NewGameViewModel
import com.twentyab.tracker.ui.navigation.TwentyAbDestinations
import com.twentyab.tracker.ui.sessions.NewSessionScreen
import com.twentyab.tracker.ui.sessions.NewSessionViewModel
import com.twentyab.tracker.ui.sessions.SessionDetailScreen
import com.twentyab.tracker.ui.sessions.SessionDetailViewModel
import com.twentyab.tracker.ui.sessions.SessionsScreen
import com.twentyab.tracker.ui.sessions.SessionsViewModel
import com.twentyab.tracker.ui.statistics.StatisticsScreen
import com.twentyab.tracker.ui.statistics.StatisticsViewModel

@Composable
fun TwentyAbApp(repository: TwentyAbRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val title = when {
        currentRoute == TwentyAbDestinations.NewSession -> "Neuer Stammtisch"
        currentRoute?.startsWith(TwentyAbDestinations.SessionDetail) == true -> "Stammtisch"
        currentRoute?.startsWith(TwentyAbDestinations.NewGame) == true -> "Neues Spiel"
        currentRoute == TwentyAbDestinations.Statistics -> "Statistik"
        else -> "20ab Tracker"
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zurück")
                        }
                    }
                },
                actions = {
                    if (currentRoute != TwentyAbDestinations.Statistics) {
                        IconButton(onClick = { navController.navigate(TwentyAbDestinations.Statistics) }) {
                            Icon(imageVector = Icons.Default.BarChart, contentDescription = "Statistik")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = TwentyAbDestinations.Sessions,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(TwentyAbDestinations.Sessions) {
                val viewModel: SessionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = TwentyAbViewModelFactory(repository)
                )
                SessionsScreen(
                    viewModel = viewModel,
                    onCreateSession = { navController.navigate(TwentyAbDestinations.NewSession) },
                    onSessionSelected = { id ->
                        navController.navigate("${TwentyAbDestinations.SessionDetail}/$id")
                    }
                )
            }
            composable(TwentyAbDestinations.NewSession) {
                val viewModel: NewSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = TwentyAbViewModelFactory(repository)
                )
                NewSessionScreen(
                    viewModel = viewModel,
                    onSessionCreated = { sessionId ->
                        navController.popBackStack()
                        navController.navigate("${TwentyAbDestinations.SessionDetail}/$sessionId")
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(
                route = "${TwentyAbDestinations.SessionDetail}/{${TwentyAbDestinations.SessionIdArg}}",
                arguments = listOf(navArgument(TwentyAbDestinations.SessionIdArg) { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong(TwentyAbDestinations.SessionIdArg) ?: return@composable
                val viewModel: SessionDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = TwentyAbViewModelFactory(repository, sessionId)
                )
                SessionDetailScreen(
                    viewModel = viewModel,
                    onAddGame = { id ->
                        navController.navigate("${TwentyAbDestinations.NewGame}/$id")
                    }
                )
            }
            composable(
                route = "${TwentyAbDestinations.NewGame}/{${TwentyAbDestinations.SessionIdArg}}",
                arguments = listOf(navArgument(TwentyAbDestinations.SessionIdArg) { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong(TwentyAbDestinations.SessionIdArg) ?: return@composable
                val viewModel: NewGameViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = TwentyAbViewModelFactory(repository, sessionId)
                )
                NewGameScreen(
                    viewModel = viewModel,
                    onGameCreated = {
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(TwentyAbDestinations.Statistics) {
                val viewModel: StatisticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = TwentyAbViewModelFactory(repository)
                )
                StatisticsScreen(viewModel = viewModel)
            }
        }
    }
}
