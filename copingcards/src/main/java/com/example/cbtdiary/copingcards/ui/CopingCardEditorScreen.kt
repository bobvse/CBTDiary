package com.example.cbtdiary.copingcards.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.copingcards.R
import com.example.cbtdiary.copingcards.domain.model.CopingCard
import com.example.cbtdiary.copingcards.domain.model.PredefinedStrategy
import com.example.cbtdiary.copingcards.domain.model.PredefinedTag
import com.example.cbtdiary.copingcards.domain.model.cardColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopingCardEditorScreen(
    viewModel: CopingCardsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.editorState.collectAsState()
    val totalSteps = 4
    var wasSaving by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaving) {
        if (wasSaving && !state.isSaving && state.errorRes == null && state.currentStep == totalSteps - 1) {
            onNavigateBack()
        }
        wasSaving = state.isSaving
    }

    if (state.showImportSheet) {
        ImportBottomSheet(
            suggestions = state.importSuggestions,
            isLoading = state.isLoadingImport,
            onSelect = viewModel::applyImport,
            onDismiss = viewModel::dismissImportSheet
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing) stringResource(R.string.editor_card_edit_title)
                        else stringResource(R.string.editor_card_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.currentStep == 0) onNavigateBack()
                        else viewModel.prevEditorStep()
                    }) {
                        Icon(
                            imageVector = if (state.currentStep == 0) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    Text(
                        text = stringResource(R.string.step_counter, state.currentStep + 1, totalSteps),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            LinearProgressIndicator(
                progress = { (state.currentStep + 1).toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "editorStep"
            ) { step ->
                when (step) {
                    0 -> FrontStep(
                        card = state.card,
                        onUpdate = viewModel::updateEditorCard,
                        onImportSmer = viewModel::loadSmerImport,
                        onImportConcept = viewModel::loadConceptImport,
                        errorRes = state.errorRes
                    )
                    1 -> BackStep(
                        card = state.card,
                        onUpdate = viewModel::updateEditorCard,
                        errorRes = state.errorRes
                    )
                    2 -> StrategiesStep(
                        card = state.card,
                        onUpdate = viewModel::updateEditorCard
                    )
                    3 -> TagsAndColorStep(
                        card = state.card,
                        onUpdate = viewModel::updateEditorCard
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.currentStep != 0) {
                    FilledTonalButton(
                        onClick = viewModel::prevEditorStep,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.cd_back))
                    }
                }
                if (state.currentStep == totalSteps - 1) {
                    Button(
                        onClick = { if (!state.isSaving) viewModel.saveCard() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_saving))
                        } else {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.editor_save))
                        }
                    }
                } else {
                    Button(
                        onClick = viewModel::nextEditorStep,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.action_next))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FrontStep(
    card: CopingCard,
    onUpdate: (CopingCard.() -> CopingCard) -> Unit,
    onImportSmer: () -> Unit,
    onImportConcept: () -> Unit,
    errorRes: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.editor_step_front),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.editor_step_front_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = card.frontText,
            onValueChange = { text -> onUpdate { copy(frontText = text) } },
            label = { Text(stringResource(R.string.editor_front_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            isError = errorRes == R.string.editor_error_front
        )

        if (errorRes == R.string.editor_error_front) {
            Text(
                text = stringResource(R.string.editor_error_front),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.editor_import_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onImportSmer),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.editor_import_smer), style = MaterialTheme.typography.labelMedium)
                }
            }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onImportConcept),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Psychology, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.editor_import_concept), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun BackStep(
    card: CopingCard,
    onUpdate: (CopingCard.() -> CopingCard) -> Unit,
    errorRes: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.editor_step_back),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.editor_step_back_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = card.backText,
            onValueChange = { text -> onUpdate { copy(backText = text) } },
            label = { Text(stringResource(R.string.editor_back_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            isError = errorRes == R.string.editor_error_back
        )

        if (errorRes == R.string.editor_error_back) {
            Text(
                text = stringResource(R.string.editor_error_back),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StrategiesStep(
    card: CopingCard,
    onUpdate: (CopingCard.() -> CopingCard) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.editor_step_strategies),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.editor_step_strategies_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PredefinedStrategy.entries.forEach { strategy ->
            val label = stringResource(strategy.labelRes)
            val desc = stringResource(strategy.descRes)
            val isSelected = card.strategies.contains(label)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable {
                        val newStrategies = if (isSelected) {
                            card.strategies - label
                        } else {
                            card.strategies + label
                        }
                        onUpdate { copy(strategies = newStrategies) }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else
                        MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = strategy.emoji, style = MaterialTheme.typography.titleMedium)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isSelected) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsAndColorStep(
    card: CopingCard,
    onUpdate: (CopingCard.() -> CopingCard) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.editor_step_tags),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.editor_step_tags_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PredefinedTag.entries.forEach { tag ->
                val label = stringResource(tag.labelRes)
                val isSelected = card.tags.contains(label)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newTags = if (isSelected) card.tags - label else card.tags + label
                        onUpdate { copy(tags = newTags) }
                    },
                    label = { Text("${tag.emoji} $label") }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.editor_card_color),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            cardColorPalette.forEachIndexed { index, palette ->
                val isSelected = card.colorIndex == index
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(palette.frontColor).copy(alpha = 0.8f))
                        .then(
                            if (isSelected)
                                Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            else Modifier
                        )
                        .clickable { onUpdate { copy(colorIndex = index) } },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportBottomSheet(
    suggestions: List<com.example.cbtdiary.copingcards.domain.model.ImportSuggestion>,
    isLoading: Boolean,
    onSelect: (com.example.cbtdiary.copingcards.domain.model.ImportSuggestion) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = stringResource(R.string.editor_import_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (suggestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.editor_import_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(suggestions) { suggestion ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(suggestion) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = suggestion.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2
                                )
                                if (suggestion.secondaryText.isNotBlank()) {
                                    Text(
                                        text = suggestion.secondaryText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// region Previews

@Preview(showBackground = true, name = "Editor Front Step")
@Composable
private fun FrontStepPreview() {
    MaterialTheme {
        FrontStep(
            card = CopingCard(),
            onUpdate = {},
            onImportSmer = {},
            onImportConcept = {},
            errorRes = null
        )
    }
}

@Preview(showBackground = true, name = "Editor Strategies Step")
@Composable
private fun StrategiesStepPreview() {
    MaterialTheme {
        StrategiesStep(
            card = CopingCard(strategies = listOf("Дыхание 4–7–8")),
            onUpdate = {}
        )
    }
}

// endregion
