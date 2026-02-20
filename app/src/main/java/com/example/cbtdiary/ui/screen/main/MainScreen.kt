package com.example.cbtdiary.ui.screen.main

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.R
import com.example.cbtdiary.copingcards.ui.CopingCardsScreen
import com.example.cbtdiary.copingcards.ui.CopingCardsViewModel
import com.example.cbtdiary.ui.screen.conceptualization.ConceptualizationScreen
import com.example.cbtdiary.ui.screen.history.HistoryScreen
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.ConceptualizationViewModel

enum class MainTab(
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DIARY(R.string.tab_diary, Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
    CONCEPTUALIZATION(R.string.tab_conceptualization, Icons.Filled.Psychology, Icons.Outlined.Psychology),
    COPING_CARDS(R.string.tab_coping_cards, Icons.Filled.Style, Icons.Outlined.Style);
}

@Composable
fun MainScreen(
    conceptViewModel: ConceptualizationViewModel,
    copingViewModel: CopingCardsViewModel,
    onNavigateToNewEntry: () -> Unit,
    onNavigateToViewEntry: (Long) -> Unit,
    onNavigateToConceptEdit: () -> Unit,
    onNavigateToConceptHistory: () -> Unit,
    onNavigateToCopingDetail: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToCopingEditor: () -> Unit,
    onNavigateToCopingQuiz: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                MainTab.entries.forEachIndexed { index, tab ->
                    val selected = selectedTab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = stringResource(tab.labelRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(tab.labelRes),
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                (fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                        scaleIn(
                            initialScale = 0.96f,
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )).togetherWith(
                    fadeOut(spring(stiffness = Spring.StiffnessMedium)) +
                            scaleOut(
                                targetScale = 0.96f,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium)
                            )
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            label = "tabContent"
        ) { tab ->
            when (tab) {
                0 -> HistoryScreen(
                    onNavigateToNewEntry = onNavigateToNewEntry,
                    onNavigateToViewEntry = onNavigateToViewEntry,
                    onNavigateToEdit = onNavigateToEdit
                )
                1 -> ConceptualizationScreen(
                    viewModel = conceptViewModel,
                    onNavigateToEdit = {
                        conceptViewModel.startEditing()
                        onNavigateToConceptEdit()
                    },
                    onNavigateToHistory = onNavigateToConceptHistory,
                    onNavigateToNew = {
                        conceptViewModel.startNew()
                        onNavigateToConceptEdit()
                    }
                )
                2 -> CopingCardsScreen(
                    viewModel = copingViewModel,
                    onNavigateToEditor = onNavigateToCopingEditor,
                    onNavigateToDetail = onNavigateToCopingDetail,
                    onNavigateToQuiz = onNavigateToCopingQuiz
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Main Screen")
@Composable
private fun MainScreenPreview() {
    CBTDiaryTheme {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    MainTab.entries.forEachIndexed { index, tab ->
                        val selected = index == 0
                        NavigationBarItem(
                            selected = selected,
                            onClick = {},
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = stringResource(tab.labelRes)
                                )
                            },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        ) { _ -> }
    }
}
