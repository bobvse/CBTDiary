package com.example.cbtdiary.ui.screen.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.model.Emotions
import com.example.cbtdiary.ui.components.SimpleFlowRow
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.ViewEntryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ViewEntryScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit = {},
    viewModel: ViewEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entryId) {
        viewModel.loadEntry(entryId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ViewEntryViewModel.ViewEvent.DeleteSuccess -> onNavigateBack()
            }
        }
    }

    ViewEntryScreenContent(
        entry = uiState.entry,
        isLoading = uiState.isLoading,
        onNavigateBack = onNavigateBack,
        onDelete = viewModel::deleteEntry,
        onEdit = onNavigateToEdit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEntryScreenContent(
    entry: DiaryEntry?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.view_entry_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.cd_edit_entry)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.cd_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (entry != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (entry.createdAt > 0) {
                    val formatter = remember {
                        DateTimeFormatter.ofPattern(
                            "d MMMM yyyy, HH:mm",
                            Locale.forLanguageTag("ru-RU")
                        )
                    }
                    val formattedDate = remember(entry.createdAt) {
                        Instant.ofEpochMilli(entry.createdAt)
                            .atZone(ZoneId.systemDefault())
                            .format(formatter)
                    }
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                SmepSection(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    title = stringResource(R.string.section_situation),
                    content = entry.situation,
                    color = MaterialTheme.colorScheme.primary
                )

                SmepSection(
                    icon = Icons.Outlined.Lightbulb,
                    title = stringResource(R.string.section_thoughts),
                    content = entry.thoughts,
                    color = Color(0xFF9C27B0)
                )

                if (entry.emotions.isNotEmpty()) {
                    EmotionViewSection(
                        emotions = entry.emotions
                    )
                }

                SmepSection(
                    icon = Icons.Outlined.Bolt,
                    title = stringResource(R.string.section_body_reaction),
                    content = entry.bodyReaction,
                    color = Color(0xFFFF9800)
                )

                SmepSection(
                    icon = Icons.AutoMirrored.Outlined.DirectionsRun,
                    title = stringResource(R.string.section_action_reaction),
                    content = entry.actionReaction,
                    color = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.view_entry_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SmepSection(
    icon: ImageVector,
    title: String,
    content: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (content.isBlank()) return

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
    }
}

@Composable
private fun EmotionViewSection(
    emotions: List<String>,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE91E63).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.section_emotions),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE91E63)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                SimpleFlowRow(
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    emotions.forEach { emotionName ->
                        val emotion = Emotions.allEmotions.find { it.name == emotionName }
                        val chipColor = if (emotion != null) {
                            Color(emotion.category.color)
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                        Surface(
                            color = chipColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = emotionName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = chipColor,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "View Entry Screen")
@Composable
private fun ViewEntryScreenPreview() {
    CBTDiaryTheme {
        ViewEntryScreenContent(
            entry = DiaryEntry(
                id = 1L,
                situation = "Партнер сказал, что я не уделяю достаточно внимания",
                thoughts = "Я плохой партнер, я всегда все делаю не так",
                emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ"),
                bodyReaction = "Напряжение в плечах, ком в горле",
                actionReaction = "Попытался спокойно объяснить свою позицию",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            isLoading = false,
            onNavigateBack = {},
            onDelete = {},
            onEdit = {}
        )
    }
}

@Preview(showBackground = true, name = "View Entry Screen - Not Found")
@Composable
private fun ViewEntryScreenNotFoundPreview() {
    CBTDiaryTheme {
        ViewEntryScreenContent(
            entry = null,
            isLoading = false,
            onNavigateBack = {},
            onDelete = {},
            onEdit = {}
        )
    }
}
