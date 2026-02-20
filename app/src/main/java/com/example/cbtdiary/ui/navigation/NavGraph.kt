package com.example.cbtdiary.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.unit.IntOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cbtdiary.auth.ui.AuthScreen
import com.example.cbtdiary.copingcards.ui.CopingCardDetailScreen
import com.example.cbtdiary.copingcards.ui.CopingCardEditorScreen
import com.example.cbtdiary.copingcards.ui.CopingCardQuizScreen
import com.example.cbtdiary.copingcards.ui.CopingCardsViewModel
import com.example.cbtdiary.ui.screen.conceptualization.ConceptualizationEditScreen
import com.example.cbtdiary.ui.screen.conceptualization.SmerImportSheet
import com.example.cbtdiary.ui.screen.conceptualization.VersionHistoryScreen
import com.example.cbtdiary.ui.screen.entry.EntryScreen
import com.example.cbtdiary.ui.screen.main.MainScreen
import com.example.cbtdiary.ui.screen.view.ViewEntryScreen
import com.example.cbtdiary.ui.viewmodel.ConceptualizationViewModel

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Main : Screen("main")
    data object NewEntry : Screen("new_entry")
    data object ViewEntry : Screen("view_entry/{entryId}") {
        fun createRoute(entryId: Long) = "view_entry/$entryId"
    }
    data object ConceptEdit : Screen("concept_edit")
    data object ConceptHistory : Screen("concept_history")
    data object CopingCardDetail : Screen("coping_detail/{cardId}") {
        fun createRoute(cardId: Long) = "coping_detail/$cardId"
    }
    data object CopingCardEditor : Screen("coping_editor")
    data object CopingCardQuiz : Screen("coping_quiz")
}

private val slideInFromEnd = spring<IntOffset>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMediumLow
)
private val slideOutToEnd = spring<IntOffset>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMedium
)

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
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Main.route,
            enterTransition = { fadeIn(spring(stiffness = Spring.StiffnessMedium)) },
            exitTransition = { fadeOut(spring(stiffness = Spring.StiffnessMedium)) }
        ) { backStackEntry ->
            val conceptViewModel: ConceptualizationViewModel = hiltViewModel(backStackEntry)
            val copingViewModel: CopingCardsViewModel = hiltViewModel(backStackEntry)

            MainScreen(
                conceptViewModel = conceptViewModel,
                copingViewModel = copingViewModel,
                onNavigateToNewEntry = { navController.navigate(Screen.NewEntry.route) },
                onNavigateToViewEntry = { entryId -> navController.navigate(Screen.ViewEntry.createRoute(entryId)) },
                onNavigateToConceptEdit = { navController.navigate(Screen.ConceptEdit.route) },
                onNavigateToConceptHistory = { navController.navigate(Screen.ConceptHistory.route) },
                onNavigateToCopingDetail = { cardId -> navController.navigate(Screen.CopingCardDetail.createRoute(cardId)) },
                onNavigateToCopingEditor = { navController.navigate(Screen.CopingCardEditor.route) },
                onNavigateToCopingQuiz = { navController.navigate(Screen.CopingCardQuiz.route) }
            )
        }

        composable(
            route = Screen.NewEntry.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, slideOutToEnd) + fadeOut()
            }
        ) {
            EntryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ViewEntry.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType }),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, slideOutToEnd) + fadeOut()
            }
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            ViewEntryScreen(entryId = entryId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ConceptEdit.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, slideOutToEnd) + fadeOut()
            }
        ) {
            val mainEntry = navController.getBackStackEntry(Screen.Main.route)
            val conceptViewModel: ConceptualizationViewModel = hiltViewModel(mainEntry)
            var showSmerImport by rememberSaveable { mutableStateOf(false) }

            ConceptualizationEditScreen(
                viewModel = conceptViewModel,
                onNavigateBack = { navController.popBackStack() },
                onOpenSmerImport = { showSmerImport = true }
            )
            if (showSmerImport) {
                SmerImportSheet(viewModel = conceptViewModel, onDismiss = { showSmerImport = false })
            }
        }

        composable(
            route = Screen.ConceptHistory.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, slideOutToEnd) + fadeOut()
            }
        ) {
            val mainEntry = navController.getBackStackEntry(Screen.Main.route)
            val conceptViewModel: ConceptualizationViewModel = hiltViewModel(mainEntry)
            VersionHistoryScreen(viewModel = conceptViewModel, onNavigateBack = { navController.popBackStack() })
        }

        // Coping Card routes
        composable(
            route = Screen.CopingCardDetail.route,
            arguments = listOf(navArgument("cardId") { type = NavType.LongType }),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, slideOutToEnd) + fadeOut()
            }
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId") ?: 0L
            val mainEntry = navController.getBackStackEntry(Screen.Main.route)
            val copingViewModel: CopingCardsViewModel = hiltViewModel(mainEntry)

            CopingCardDetailScreen(
                cardId = cardId,
                viewModel = copingViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(Screen.CopingCardEditor.route)
                }
            )
        }

        composable(
            route = Screen.CopingCardEditor.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, slideOutToEnd) + fadeOut()
            }
        ) {
            val mainEntry = navController.getBackStackEntry(Screen.Main.route)
            val copingViewModel: CopingCardsViewModel = hiltViewModel(mainEntry)

            CopingCardEditorScreen(
                viewModel = copingViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CopingCardQuiz.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, slideInFromEnd) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, slideOutToEnd) + fadeOut()
            }
        ) {
            val mainEntry = navController.getBackStackEntry(Screen.Main.route)
            val copingViewModel: CopingCardsViewModel = hiltViewModel(mainEntry)

            CopingCardQuizScreen(
                viewModel = copingViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
