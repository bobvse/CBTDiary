package com.example.cbtdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cbtdiary.ui.screen.history.HistoryScreen
import com.example.cbtdiary.ui.screen.entry.EntryScreen

sealed class Screen(val route: String) {
    object History : Screen("history")
    object Entry : Screen("entry/{entryId}") {
        fun createRoute(entryId: Long = 0L) = "entry/$entryId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.History.route
    ) {
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToEntry = { entryId ->
                    navController.navigate(Screen.Entry.createRoute(entryId))
                }
            )
        }
        
        composable(
            route = Screen.Entry.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            EntryScreen(
                entryId = entryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
