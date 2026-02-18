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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.cbtdiary.R
import com.example.cbtdiary.ui.screen.conceptualization.ConceptualizationScreen
import com.example.cbtdiary.ui.screen.copingcards.CopingCardsScreen
import com.example.cbtdiary.ui.screen.history.HistoryScreen
import com.example.cbtdiary.ui.theme.CBTDiaryTheme

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
    onNavigateToNewEntry: () -> Unit,
    onNavigateToViewEntry: (Long) -> Unit
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
                    onNavigateToViewEntry = onNavigateToViewEntry
                )
                1 -> ConceptualizationScreen()
                2 -> CopingCardsScreen()
            }
        }
    }
}

@Preview(showBackground = true, name = "Main Screen")
@Composable
private fun MainScreenPreview() {
    CBTDiaryTheme {
        MainScreen(
            onNavigateToNewEntry = {},
            onNavigateToViewEntry = {}
        )
    }
}
