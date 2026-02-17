package com.example.cbtdiary.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cbtdiary.auth.ui.AuthScreen
import com.example.cbtdiary.ui.screen.entry.EntryScreen
import com.example.cbtdiary.ui.screen.history.HistoryScreen
import com.example.cbtdiary.ui.screen.view.ViewEntryScreen

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object History : Screen("history")
    data object NewEntry : Screen("new_entry")
    data object ViewEntry : Screen("view_entry/{entryId}") {
        fun createRoute(entryId: Long) = "view_entry/$entryId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(
            route = Screen.Auth.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.History.route,
            enterTransition = { fadeIn(spring(stiffness = Spring.StiffnessMedium)) },
            exitTransition = { fadeOut(spring(stiffness = Spring.StiffnessMedium)) }
        ) {
            HistoryScreen(
                onNavigateToNewEntry = {
                    navController.navigate(Screen.NewEntry.route)
                },
                onNavigateToViewEntry = { entryId ->
                    navController.navigate(Screen.ViewEntry.createRoute(entryId))
                }
            )
        }

        composable(
            route = Screen.NewEntry.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut()
            }
        ) {
            EntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ViewEntry.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut()
            }
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            ViewEntryScreen(
                entryId = entryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
