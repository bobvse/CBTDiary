package com.example.cbtdiary.ui.screen.conceptualization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.SmerSuggestionItem
import com.example.cbtdiary.domain.model.SmerSuggestions
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.ConceptualizationViewModel
import com.example.cbtdiary.ui.viewmodel.SmerImportState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmerImportSheet(
    viewModel: ConceptualizationViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.smerImportState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadSmerSuggestions()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        SmerImportContent(
            state = state,
            onToggleThought = viewModel::toggleThoughtSelection,
            onToggleEmotion = viewModel::toggleEmotionSelection,
            onToggleSituation = viewModel::toggleSituationSelection,
            onImport = {
                viewModel.applyImport()
                onDismiss()
            },
            totalSelected = viewModel.totalSelectedImports
        )
    }
}

@Composable
private fun SmerImportContent(
    state: SmerImportState,
    onToggleThought: (String) -> Unit,
    onToggleEmotion: (String) -> Unit,
    onToggleSituation: (String) -> Unit,
    onImport: () -> Unit,
    totalSelected: Int
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.smer_import_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.suggestions.thoughts.isEmpty() && state.suggestions.emotions.isEmpty() && state.suggestions.situations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.smer_import_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            val tabs = listOf(
                ImportTab(R.string.smer_import_thoughts, Icons.Outlined.Lightbulb),
                ImportTab(R.string.smer_import_emotions, Icons.Outlined.Mood),
                ImportTab(R.string.smer_import_situations, Icons.Outlined.Place)
            )

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(stringResource(tab.labelRes)) },
                        icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val currentItems: List<SmerSuggestionItem>
            val selectedSet: Set<String>
            val onToggle: (String) -> Unit

            when (selectedTab) {
                0 -> {
                    currentItems = state.suggestions.thoughts
                    selectedSet = state.selectedThoughts
                    onToggle = onToggleThought
                }
                1 -> {
                    currentItems = state.suggestions.emotions
                    selectedSet = state.selectedEmotions
                    onToggle = onToggleEmotion
                }
                else -> {
                    currentItems = state.suggestions.situations
                    selectedSet = state.selectedSituations
                    onToggle = onToggleSituation
                }
            }

            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(currentItems, key = { it.text }) { item ->
                    SuggestionRow(
                        item = item,
                        selected = item.text in selectedSet,
                        onToggle = { onToggle(item.text) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledTonalButton(
            onClick = onImport,
            modifier = Modifier.fillMaxWidth(),
            enabled = totalSelected > 0,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.smer_import_selected, totalSelected))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class ImportTab(val labelRes: Int, val icon: ImageVector)

@Composable
private fun SuggestionRow(
    item: SmerSuggestionItem,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Text(
                    text = stringResource(R.string.smer_import_frequency, item.frequency),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// region Previews

@Preview(showBackground = true, name = "SMER Import Content")
@Composable
private fun SmerImportContentPreview() {
    CBTDiaryTheme {
        SmerImportContent(
            state = SmerImportState(
                suggestions = SmerSuggestions(
                    thoughts = listOf(
                        SmerSuggestionItem("Я не справлюсь", 5),
                        SmerSuggestionItem("Все думают что я глупый", 3),
                        SmerSuggestionItem("Ничего не получится", 2)
                    ),
                    emotions = listOf(
                        SmerSuggestionItem("Тревога", 7),
                        SmerSuggestionItem("Стыд", 4)
                    ),
                    situations = listOf(
                        SmerSuggestionItem("Совещание на работе", 3)
                    )
                ),
                selectedThoughts = setOf("Я не справлюсь")
            ),
            onToggleThought = {},
            onToggleEmotion = {},
            onToggleSituation = {},
            onImport = {},
            totalSelected = 1
        )
    }
}

@Preview(showBackground = true, name = "SMER Import Empty")
@Composable
private fun SmerImportEmptyPreview() {
    CBTDiaryTheme {
        SmerImportContent(
            state = SmerImportState(),
            onToggleThought = {},
            onToggleEmotion = {},
            onToggleSituation = {},
            onImport = {},
            totalSelected = 0
        )
    }
}

// endregion
