package com.example.cbtdiary.ui.screen.conceptualization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.domain.model.Alternative
import com.example.cbtdiary.domain.model.AutomaticThought
import com.example.cbtdiary.domain.model.BackgroundEvent
import com.example.cbtdiary.domain.model.CognitiveDistortion
import com.example.cbtdiary.domain.model.CoreBelief
import com.example.cbtdiary.domain.model.EmotionEntry
import com.example.cbtdiary.domain.model.IntermediateBelief
import com.example.cbtdiary.domain.model.Trigger
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.ConceptualizationViewModel
import com.example.cbtdiary.ui.viewmodel.EditorState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptualizationEditScreen(
    viewModel: ConceptualizationViewModel,
    onNavigateBack: () -> Unit,
    onOpenSmerImport: () -> Unit
) {
    val state by viewModel.editorState.collectAsState()

    if (state.showVersionNoteDialog) {
        VersionNoteDialog(
            onConfirm = { note ->
                viewModel.confirmSave(note)
                onNavigateBack()
            },
            onDismiss = { viewModel.cancelSaveDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.editor_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSmerImport) {
                        Icon(Icons.Outlined.FileUpload, contentDescription = stringResource(R.string.editor_import_smer))
                    }
                    IconButton(onClick = { viewModel.requestSave() }) {
                        Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.editor_save))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        EditorContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onUpdateBackground = viewModel::updateBackground,
            onUpdateCoreBeliefs = viewModel::updateCoreBeliefs,
            onUpdateIntermediateBeliefs = viewModel::updateIntermediateBeliefs,
            onUpdateCopingStrategies = viewModel::updateCopingStrategies,
            onUpdateTriggers = viewModel::updateTriggers,
            onUpdateAutomaticThoughts = viewModel::updateAutomaticThoughts,
            onUpdateEmotions = viewModel::updateEmotions,
            onUpdateBehavioralPatterns = viewModel::updateBehavioralPatterns,
            onUpdateAlternatives = viewModel::updateAlternatives,
            onUpdateStrengths = viewModel::updateStrengths,
            onUpdateGoals = viewModel::updateGoals
        )
    }
}

@Composable
private fun EditorContent(
    state: EditorState,
    modifier: Modifier = Modifier,
    onUpdateBackground: (List<BackgroundEvent>) -> Unit,
    onUpdateCoreBeliefs: (List<CoreBelief>) -> Unit,
    onUpdateIntermediateBeliefs: (List<IntermediateBelief>) -> Unit,
    onUpdateCopingStrategies: (List<String>) -> Unit,
    onUpdateTriggers: (List<Trigger>) -> Unit,
    onUpdateAutomaticThoughts: (List<AutomaticThought>) -> Unit,
    onUpdateEmotions: (List<EmotionEntry>) -> Unit,
    onUpdateBehavioralPatterns: (List<String>) -> Unit,
    onUpdateAlternatives: (List<Alternative>) -> Unit,
    onUpdateStrengths: (List<String>) -> Unit,
    onUpdateGoals: (List<String>) -> Unit
) {
    val draft = state.draft

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 1. Background
        item {
            EditorSection(
                titleRes = R.string.section_background,
                hintRes = R.string.section_background_hint,
                color = Color(0xFF78909C),
                itemCount = draft.background.size,
                summaryItems = draft.background.map { it.text }
            ) {
                BackgroundEditor(
                    items = draft.background,
                    onUpdate = onUpdateBackground
                )
            }
        }

        // 2. Core Beliefs
        item {
            EditorSection(
                titleRes = R.string.section_core_beliefs,
                hintRes = R.string.section_core_beliefs_hint,
                color = Color(0xFFE53935),
                itemCount = draft.coreBeliefs.size,
                summaryItems = draft.coreBeliefs.map { "«${it.text}» (${it.strength}%)" }
            ) {
                CoreBeliefsEditor(
                    items = draft.coreBeliefs,
                    onUpdate = onUpdateCoreBeliefs
                )
            }
        }

        // 3. Intermediate Beliefs
        item {
            EditorSection(
                titleRes = R.string.section_intermediate_beliefs,
                hintRes = R.string.section_intermediate_beliefs_hint,
                color = Color(0xFFFF9800),
                itemCount = draft.intermediateBeliefs.size,
                summaryItems = draft.intermediateBeliefs.map { it.rule }
            ) {
                IntermediateBeliefEditor(
                    items = draft.intermediateBeliefs,
                    onUpdate = onUpdateIntermediateBeliefs
                )
            }
        }

        // 4. Coping Strategies
        item {
            EditorSection(
                titleRes = R.string.section_coping_strategies,
                hintRes = R.string.section_coping_strategies_hint,
                color = Color(0xFFFFC107),
                itemCount = draft.copingStrategies.size,
                summaryItems = draft.copingStrategies
            ) {
                CopingStrategiesEditor(
                    items = draft.copingStrategies,
                    onUpdate = onUpdateCopingStrategies
                )
            }
        }

        // 5. Triggers
        item {
            EditorSection(
                titleRes = R.string.section_triggers,
                hintRes = R.string.section_triggers_hint,
                color = Color(0xFF1E88E5),
                itemCount = draft.triggers.size,
                summaryItems = draft.triggers.map { it.text }
            ) {
                TriggersEditor(
                    items = draft.triggers,
                    onUpdate = onUpdateTriggers
                )
            }
        }

        // 6. Automatic Thoughts
        item {
            EditorSection(
                titleRes = R.string.section_automatic_thoughts,
                hintRes = R.string.section_automatic_thoughts_hint,
                color = Color(0xFF7B1FA2),
                itemCount = draft.automaticThoughts.size,
                summaryItems = draft.automaticThoughts.map { it.text }
            ) {
                AutomaticThoughtsEditor(
                    items = draft.automaticThoughts,
                    onUpdate = onUpdateAutomaticThoughts
                )
            }
        }

        // 7. Emotions
        item {
            EditorSection(
                titleRes = R.string.section_emotions_reactions,
                hintRes = R.string.section_emotions_reactions_hint,
                color = Color(0xFFE91E63),
                itemCount = draft.emotions.size,
                summaryItems = draft.emotions.map { "${it.name} (${it.intensity}%)" }
            ) {
                EmotionsEditor(
                    items = draft.emotions,
                    onUpdate = onUpdateEmotions
                )
            }
        }

        // 8. Behavioral Patterns
        item {
            EditorSection(
                titleRes = R.string.section_behavioral_patterns,
                hintRes = R.string.section_behavioral_patterns_hint,
                color = Color(0xFF8D6E63),
                itemCount = draft.behavioralPatterns.size,
                summaryItems = draft.behavioralPatterns
            ) {
                StringListEditor(
                    items = draft.behavioralPatterns,
                    labelRes = R.string.editor_text_item,
                    onUpdate = onUpdateBehavioralPatterns
                )
            }
        }

        // 9. Alternatives
        item {
            EditorSection(
                titleRes = R.string.section_alternatives,
                hintRes = R.string.section_alternatives_hint,
                color = Color(0xFF43A047),
                itemCount = draft.alternatives.size,
                summaryItems = draft.alternatives.map { "«${it.newThought}»" }
            ) {
                AlternativesEditor(
                    items = draft.alternatives,
                    onUpdate = onUpdateAlternatives
                )
            }
        }

        // 10. Strengths
        item {
            EditorSection(
                titleRes = R.string.section_strengths,
                hintRes = R.string.section_strengths_hint,
                color = Color(0xFF26A69A),
                itemCount = draft.strengths.size,
                summaryItems = draft.strengths
            ) {
                StringListEditor(
                    items = draft.strengths,
                    labelRes = R.string.editor_text_item,
                    onUpdate = onUpdateStrengths
                )
            }
        }

        // 11. Goals
        item {
            EditorSection(
                titleRes = R.string.section_goals,
                hintRes = R.string.section_goals_hint,
                color = Color(0xFF2196F3),
                itemCount = draft.goals.size,
                summaryItems = draft.goals
            ) {
                StringListEditor(
                    items = draft.goals,
                    labelRes = R.string.editor_text_item,
                    onUpdate = onUpdateGoals
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun EditorSection(
    titleRes: Int,
    hintRes: Int,
    color: Color,
    itemCount: Int = 0,
    summaryItems: List<String> = emptyList(),
    content: @Composable () -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val hasSavedData = itemCount > 0 && !isEditing

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasSavedData) color.copy(alpha = 0.06f)
            else color.copy(alpha = 0.04f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditing = !isEditing },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = color
                        )
                        if (hasSavedData) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = color.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "$itemCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(hintRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (hasSavedData) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Icon(
                    imageVector = if (isEditing) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.6f)
                )
            }

            if (hasSavedData && summaryItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                summaryItems.take(3).forEach { item ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(color.copy(alpha = 0.4f))
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (summaryItems.size > 3) {
                    Text(
                        text = "и ещё ${summaryItems.size - 3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = color.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isEditing) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    content()
                    Spacer(modifier = Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = { isEditing = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = color.copy(alpha = 0.12f),
                            contentColor = color
                        )
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.section_save_button))
                    }
                }
            }
        }
    }
}

// region Section-specific editors

@Composable
private fun BackgroundEditor(
    items: List<BackgroundEvent>,
    onUpdate: (List<BackgroundEvent>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = event.text,
                    onValueChange = { text ->
                        onUpdate(items.toMutableList().also { it[index] = event.copy(text = text) })
                    },
                    label = { Text(stringResource(R.string.editor_event_text)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                IconButton(onClick = {
                    onUpdate(items.toMutableList().also { it.removeAt(index) })
                }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(20.dp))
                }
            }
        }
        AddItemButton {
            onUpdate(items + BackgroundEvent(""))
        }
    }
}

@Composable
private fun CoreBeliefsEditor(
    items: List<CoreBelief>,
    onUpdate: (List<CoreBelief>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, belief ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = belief.text,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = belief.copy(text = text) })
                        },
                        label = { Text(stringResource(R.string.editor_belief_text)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    IconButton(onClick = {
                        onUpdate(items.toMutableList().also { it.removeAt(index) })
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    text = stringResource(R.string.editor_belief_strength, belief.strength),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Slider(
                    value = belief.strength.toFloat(),
                    onValueChange = { value ->
                        onUpdate(items.toMutableList().also { it[index] = belief.copy(strength = value.roundToInt()) })
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFE53935),
                        activeTrackColor = Color(0xFFE53935)
                    )
                )
            }
        }
        AddItemButton {
            onUpdate(items + CoreBelief(""))
        }
    }
}

@Composable
private fun IntermediateBeliefEditor(
    items: List<IntermediateBelief>,
    onUpdate: (List<IntermediateBelief>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, belief ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            onUpdate(items.toMutableList().also { it.removeAt(index) })
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(18.dp))
                        }
                    }
                    OutlinedTextField(
                        value = belief.rule,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = belief.copy(rule = text) })
                        },
                        label = { Text(stringResource(R.string.editor_rule)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    OutlinedTextField(
                        value = belief.assumption,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = belief.copy(assumption = text) })
                        },
                        label = { Text(stringResource(R.string.editor_assumption)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    OutlinedTextField(
                        value = belief.compensation,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = belief.copy(compensation = text) })
                        },
                        label = { Text(stringResource(R.string.editor_compensation)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }
            }
        }
        AddItemButton {
            onUpdate(items + IntermediateBelief(""))
        }
    }
}

@Composable
private fun TriggersEditor(
    items: List<Trigger>,
    onUpdate: (List<Trigger>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, trigger ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = trigger.text,
                    onValueChange = { text ->
                        onUpdate(items.toMutableList().also { it[index] = trigger.copy(text = text) })
                    },
                    label = { Text(stringResource(R.string.editor_trigger_text)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                IconButton(onClick = {
                    onUpdate(items.toMutableList().also { it.removeAt(index) })
                }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(20.dp))
                }
            }
        }
        AddItemButton {
            onUpdate(items + Trigger(""))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutomaticThoughtsEditor(
    items: List<AutomaticThought>,
    onUpdate: (List<AutomaticThought>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, thought ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            onUpdate(items.toMutableList().also { it.removeAt(index) })
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(18.dp))
                        }
                    }
                    OutlinedTextField(
                        value = thought.text,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = thought.copy(text = text) })
                        },
                        label = { Text(stringResource(R.string.editor_thought_text)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    var distortionExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = distortionExpanded,
                        onExpandedChange = { distortionExpanded = !distortionExpanded }
                    ) {
                        OutlinedTextField(
                            value = thought.distortionType?.let { stringResource(it.labelRes) }
                                ?: stringResource(R.string.editor_distortion_none),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.editor_distortion_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = distortionExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = distortionExpanded,
                            onDismissRequest = { distortionExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.editor_distortion_none)) },
                                onClick = {
                                    onUpdate(items.toMutableList().also { it[index] = thought.copy(distortionType = null) })
                                    distortionExpanded = false
                                }
                            )
                            CognitiveDistortion.entries.forEach { distortion ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                stringResource(distortion.labelRes),
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                stringResource(distortion.descriptionRes),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onUpdate(items.toMutableList().also { it[index] = thought.copy(distortionType = distortion) })
                                        distortionExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        AddItemButton {
            onUpdate(items + AutomaticThought(""))
        }
    }
}

@Composable
private fun EmotionsEditor(
    items: List<EmotionEntry>,
    onUpdate: (List<EmotionEntry>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, emotion ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = emotion.name,
                        onValueChange = { name ->
                            onUpdate(items.toMutableList().also { it[index] = emotion.copy(name = name) })
                        },
                        label = { Text(stringResource(R.string.editor_emotion_name)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    IconButton(onClick = {
                        onUpdate(items.toMutableList().also { it.removeAt(index) })
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    text = stringResource(R.string.editor_emotion_intensity, emotion.intensity),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Slider(
                    value = emotion.intensity.toFloat(),
                    onValueChange = { value ->
                        onUpdate(items.toMutableList().also { it[index] = emotion.copy(intensity = value.roundToInt()) })
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFE91E63),
                        activeTrackColor = Color(0xFFE91E63)
                    )
                )
            }
        }
        AddItemButton {
            onUpdate(items + EmotionEntry(""))
        }
    }
}

@Composable
private fun AlternativesEditor(
    items: List<Alternative>,
    onUpdate: (List<Alternative>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, alt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            onUpdate(items.toMutableList().also { it.removeAt(index) })
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(18.dp))
                        }
                    }
                    OutlinedTextField(
                        value = alt.oldThought,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = alt.copy(oldThought = text) })
                        },
                        label = { Text(stringResource(R.string.editor_old_thought)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    OutlinedTextField(
                        value = alt.newThought,
                        onValueChange = { text ->
                            onUpdate(items.toMutableList().also { it[index] = alt.copy(newThought = text) })
                        },
                        label = { Text(stringResource(R.string.editor_new_thought)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    Text(
                        text = stringResource(R.string.editor_believability, alt.believability),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = alt.believability.toFloat(),
                        onValueChange = { value ->
                            onUpdate(items.toMutableList().also { it[index] = alt.copy(believability = value.roundToInt()) })
                        },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF43A047),
                            activeTrackColor = Color(0xFF43A047)
                        )
                    )
                }
            }
        }
        AddItemButton {
            onUpdate(items + Alternative(newThought = ""))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CopingStrategiesEditor(
    items: List<String>,
    onUpdate: (List<String>) -> Unit
) {
    val predefined = listOf(
        "Дыхание 4–7–8" to "💨",
        "Прогрессивная мышечная релаксация" to "💪",
        "Заземление (5-4-3-2-1)" to "🌍",
        "Факт vs страх" to "🧠",
        "Позитивный самодиалог" to "💬",
        "Физическая активность" to "🏃",
        "Отвлечение" to "🎨",
        "Социальная поддержка" to "🤝"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.editor_predefined_strategies),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            predefined.forEach { (label, emoji) ->
                val isSelected = items.contains(label)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newItems = if (isSelected) items - label else items + label
                        onUpdate(newItems)
                    },
                    label = { Text("$emoji $label") }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val customItems = items.filter { item -> predefined.none { it.first == item } }

        StringListEditor(
            items = customItems,
            labelRes = R.string.editor_text_item,
            onUpdate = { newCustom ->
                val predefinedSelected = items.filter { item -> predefined.any { it.first == item } }
                onUpdate(predefinedSelected + newCustom)
            }
        )
    }
}

@Composable
internal fun StringListEditor(
    items: List<String>,
    labelRes: Int,
    onUpdate: (List<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = item,
                    onValueChange = { text ->
                        onUpdate(items.toMutableList().also { it[index] = text })
                    },
                    label = { Text(stringResource(labelRes)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                IconButton(onClick = {
                    onUpdate(items.toMutableList().also { it.removeAt(index) })
                }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.editor_remove_item), modifier = Modifier.size(20.dp))
                }
            }
        }
        AddItemButton {
            onUpdate(items + "")
        }
    }
}

// endregion

@Composable
private fun AddItemButton(onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.editor_add_item))
    }
}

@Composable
private fun VersionNoteDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.version_save_title)) },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.version_save_hint)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(note) }) {
                Text(stringResource(R.string.version_save_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.version_save_cancel))
            }
        }
    )
}

// region Previews

@Preview(showBackground = true, name = "Editor Content")
@Composable
private fun EditorContentPreview() {
    CBTDiaryTheme {
        EditorContent(
            state = EditorState(
                draft = Conceptualization(
                    coreBeliefs = listOf(CoreBelief("Я недостаточно хорош", 70)),
                    triggers = listOf(Trigger("Критика")),
                    automaticThoughts = listOf(AutomaticThought("Я не справлюсь", CognitiveDistortion.CATASTROPHIZING))
                )
            ),
            modifier = Modifier.fillMaxSize(),
            onUpdateBackground = {},
            onUpdateCoreBeliefs = {},
            onUpdateIntermediateBeliefs = {},
            onUpdateCopingStrategies = {},
            onUpdateTriggers = {},
            onUpdateAutomaticThoughts = {},
            onUpdateEmotions = {},
            onUpdateBehavioralPatterns = {},
            onUpdateAlternatives = {},
            onUpdateStrengths = {},
            onUpdateGoals = {}
        )
    }
}

@Preview(showBackground = true, name = "Version Note Dialog")
@Composable
private fun VersionNoteDialogPreview() {
    CBTDiaryTheme {
        VersionNoteDialog(onConfirm = {}, onDismiss = {})
    }
}

// endregion
