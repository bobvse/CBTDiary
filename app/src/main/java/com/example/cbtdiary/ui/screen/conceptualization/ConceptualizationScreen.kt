package com.example.cbtdiary.ui.screen.conceptualization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.Alternative
import com.example.cbtdiary.domain.model.AutomaticThought
import com.example.cbtdiary.domain.model.BackgroundEvent
import com.example.cbtdiary.domain.model.CognitiveDistortion
import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.domain.model.CoreBelief
import com.example.cbtdiary.domain.model.EmotionEntry
import com.example.cbtdiary.domain.model.IntermediateBelief
import com.example.cbtdiary.domain.model.Trigger
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.ConceptualizationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptualizationScreen(
    viewModel: ConceptualizationViewModel = hiltViewModel(),
    onNavigateToEdit: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val state by viewModel.conceptState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.conceptualization_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (state.conceptualization != null) {
                        IconButton(onClick = onNavigateToEdit) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit_concept))
                        }
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(Icons.Filled.History, contentDescription = stringResource(R.string.cd_version_history))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.conceptualization == null || state.conceptualization?.isEmpty == true -> {
                EmptyConceptualizationView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onCreateClick = onNavigateToEdit
                )
            }
            else -> {
                BeckDiagramView(
                    conceptualization = state.conceptualization!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun EmptyConceptualizationView(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.conceptualization_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.conceptualization_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(onClick = onCreateClick) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.conceptualization_create))
        }
    }
}

@Composable
private fun BeckDiagramView(
    conceptualization: Conceptualization,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember {
        SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru-RU"))
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.conceptualization_version, conceptualization.version),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(
                        R.string.conceptualization_updated,
                        dateFormat.format(Date(conceptualization.updatedAt))
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        val sections = buildBeckSections(conceptualization)

        sections.forEachIndexed { index, section ->
            item(key = "section_$index") {
                DiagramSectionCard(section = section)
                if (index < sections.lastIndex) {
                    ConnectionLine()
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

data class BeckSection(
    val titleRes: Int,
    val icon: ImageVector,
    val color: Color,
    val items: List<String>,
    val strengthBars: List<Pair<String, Int>> = emptyList(),
    val tags: List<String> = emptyList()
)

private fun buildBeckSections(c: Conceptualization): List<BeckSection> {
    return listOf(
        BeckSection(
            titleRes = R.string.section_background,
            icon = Icons.Outlined.Timeline,
            color = Color(0xFF78909C),
            items = c.background.map { "${it.text}${if (it.period.isNotBlank()) " (${it.period})" else ""}" }
        ),
        BeckSection(
            titleRes = R.string.section_core_beliefs,
            icon = Icons.Outlined.Psychology,
            color = Color(0xFFE53935),
            items = c.coreBeliefs.map { "«${it.text}»" },
            strengthBars = c.coreBeliefs.map { it.text to it.strength }
        ),
        BeckSection(
            titleRes = R.string.section_intermediate_beliefs,
            icon = Icons.Outlined.Shield,
            color = Color(0xFFFF9800),
            items = c.intermediateBeliefs.map { it.rule }
        ),
        BeckSection(
            titleRes = R.string.section_coping_strategies,
            icon = Icons.Outlined.FitnessCenter,
            color = Color(0xFFFFC107),
            items = c.copingStrategies
        ),
        BeckSection(
            titleRes = R.string.section_triggers,
            icon = Icons.Outlined.FlashOn,
            color = Color(0xFF1E88E5),
            items = c.triggers.map { it.text }
        ),
        BeckSection(
            titleRes = R.string.section_automatic_thoughts,
            icon = Icons.Outlined.Lightbulb,
            color = Color(0xFF7B1FA2),
            items = c.automaticThoughts.map { it.text },
            tags = c.automaticThoughts.mapNotNull { it.distortionType?.name }
        ),
        BeckSection(
            titleRes = R.string.section_emotions_reactions,
            icon = Icons.Outlined.Spa,
            color = Color(0xFFE91E63),
            items = c.emotions.map { "${it.name} (${it.intensity}%)" },
            strengthBars = c.emotions.map { it.name to it.intensity }
        ),
        BeckSection(
            titleRes = R.string.section_behavioral_patterns,
            icon = Icons.Outlined.AutoAwesome,
            color = Color(0xFF8D6E63),
            items = c.behavioralPatterns
        ),
        BeckSection(
            titleRes = R.string.section_alternatives,
            icon = Icons.Outlined.Star,
            color = Color(0xFF43A047),
            items = c.alternatives.map { "«${it.newThought}» (${it.believability}%)" }
        ),
        BeckSection(
            titleRes = R.string.section_strengths,
            icon = Icons.Outlined.FitnessCenter,
            color = Color(0xFF26A69A),
            items = c.strengths
        ),
        BeckSection(
            titleRes = R.string.section_goals,
            icon = Icons.Outlined.Flag,
            color = Color(0xFF2196F3),
            items = c.goals
        )
    ).filter { it.items.isNotEmpty() || it.strengthBars.isNotEmpty() }
}

@Composable
private fun DiagramSectionCard(section: BeckSection) {
    var expanded by remember { mutableStateOf(false) }
    val maxPreviewItems = 3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = section.color.copy(alpha = 0.06f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(section.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = section.color
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(section.titleRes),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = section.color
                    )
                    if (!expanded && section.items.size > maxPreviewItems) {
                        Text(
                            text = section.items.take(2).joinToString(", ") +
                                    " " + stringResource(R.string.conceptualization_and_more, section.items.size - 2),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                if (section.items.size > maxPreviewItems || section.strengthBars.isNotEmpty()) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = section.color.copy(alpha = 0.6f)
                    )
                }
            }

            val itemsToShow = if (expanded) section.items else section.items.take(maxPreviewItems)

            AnimatedVisibility(
                visible = itemsToShow.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsToShow.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(section.color.copy(alpha = 0.4f))
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (expanded && section.strengthBars.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                section.strengthBars.forEach { (label, value) ->
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$value%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = section.color
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { value / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = section.color,
                            trackColor = section.color.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionLine() {
    val color = MaterialTheme.colorScheme.outlineVariant
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        val centerX = size.width / 2
        drawLine(
            color = color,
            start = Offset(centerX, 0f),
            end = Offset(centerX, size.height),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )
        val arrowSize = 6.dp.toPx()
        drawLine(color = color, start = Offset(centerX - arrowSize, size.height - arrowSize), end = Offset(centerX, size.height), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(centerX + arrowSize, size.height - arrowSize), end = Offset(centerX, size.height), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
    }
}

// region Previews

@Preview(showBackground = true, name = "Empty Conceptualization")
@Composable
private fun EmptyConceptualizationPreview() {
    CBTDiaryTheme {
        EmptyConceptualizationView(
            modifier = Modifier.fillMaxSize(),
            onCreateClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Beck Diagram")
@Composable
private fun BeckDiagramPreview() {
    CBTDiaryTheme {
        BeckDiagramView(
            conceptualization = Conceptualization(
                version = 3,
                background = listOf(
                    BackgroundEvent("Критика со стороны родителей", "Детство"),
                    BackgroundEvent("Буллинг в школе", "Подростковый возраст")
                ),
                coreBeliefs = listOf(
                    CoreBelief("Я недостаточно хорош", 75),
                    CoreBelief("Мир опасен", 60)
                ),
                intermediateBeliefs = listOf(
                    IntermediateBelief("Если я не буду идеальным, меня отвергнут")
                ),
                copingStrategies = listOf("Избегание", "Перфекционизм"),
                triggers = listOf(Trigger("Критика на работе"), Trigger("Ошибка в проекте")),
                automaticThoughts = listOf(
                    AutomaticThought("Я никогда не справлюсь", CognitiveDistortion.CATASTROPHIZING),
                    AutomaticThought("Все думают, что я глупый", CognitiveDistortion.MIND_READING)
                ),
                emotions = listOf(
                    EmotionEntry("Тревога", 80),
                    EmotionEntry("Стыд", 65)
                ),
                behavioralPatterns = listOf("Откладывание дел", "Уход от конфликтов"),
                alternatives = listOf(
                    Alternative("Я никогда не справлюсь", "Я справлялся раньше и справлюсь снова", 40)
                ),
                strengths = listOf("Упорство", "Эмпатия"),
                goals = listOf("Снизить самокритику", "Развить уверенность"),
                updatedAt = System.currentTimeMillis()
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, name = "Section Card")
@Composable
private fun SectionCardPreview() {
    CBTDiaryTheme {
        DiagramSectionCard(
            section = BeckSection(
                titleRes = R.string.section_core_beliefs,
                icon = Icons.Outlined.Psychology,
                color = Color(0xFFE53935),
                items = listOf("«Я недостаточно хорош»", "«Мир опасен»"),
                strengthBars = listOf("Я недостаточно хорош" to 75, "Мир опасен" to 60)
            )
        )
    }
}

// endregion
