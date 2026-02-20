package com.example.cbtdiary.ui.screen.conceptualization

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.History
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.ConceptualizationViewModel
import com.example.cbtdiary.ui.viewmodel.VersionsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionHistoryScreen(
    viewModel: ConceptualizationViewModel,
    onNavigateBack: () -> Unit,
    onSelectVersion: (Long) -> Unit = {}
) {
    val state by viewModel.versionsState.collectAsState()
    val currentConceptState by viewModel.conceptState.collectAsState()
    val currentVersion = currentConceptState.conceptualization?.version

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.version_history_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        VersionHistoryContent(
            state = state,
            currentVersion = currentVersion,
            onDelete = viewModel::deleteVersion,
            onSelect = onSelectVersion,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun VersionHistoryContent(
    state: VersionsState,
    currentVersion: Int?,
    onDelete: (Long) -> Unit,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.versions.isEmpty() -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.version_history_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                itemsIndexed(state.versions, key = { _, v -> v.id }) { index, version ->
                    VersionTimelineItem(
                        version = version,
                        isCurrent = version.version == currentVersion,
                        isFirst = index == 0,
                        isLast = index == state.versions.lastIndex,
                        onDelete = { onDelete(version.id) },
                        onSelect = { onSelect(version.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun VersionTimelineItem(
    version: Conceptualization,
    isCurrent: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru-RU"))
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.version_delete_title)) },
            text = { Text(stringResource(R.string.version_delete_message, version.version)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline track
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(
                modifier = Modifier
                    .size(if (isCurrent) 16.dp else 12.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrent) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Version card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 4.dp)
                .offset(y = 4.dp)
                .clickable { onSelect() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.conceptualization_version, version.version),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                        if (isCurrent) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = stringResource(R.string.version_current),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (!isCurrent) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.cd_delete),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateFormat.format(Date(version.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (version.versionNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = version.versionNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val sectionCount = countFilledSections(version)
                if (sectionCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.progress_versions_count, sectionCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun countFilledSections(c: Conceptualization): Int {
    var count = 0
    if (c.background.isNotEmpty()) count++
    if (c.coreBeliefs.isNotEmpty()) count++
    if (c.intermediateBeliefs.isNotEmpty()) count++
    if (c.copingStrategies.isNotEmpty()) count++
    if (c.triggers.isNotEmpty()) count++
    if (c.automaticThoughts.isNotEmpty()) count++
    if (c.emotions.isNotEmpty()) count++
    if (c.behavioralPatterns.isNotEmpty()) count++
    if (c.alternatives.isNotEmpty()) count++
    if (c.strengths.isNotEmpty()) count++
    if (c.goals.isNotEmpty()) count++
    return count
}

// region Previews

@Preview(showBackground = true, name = "Version History")
@Composable
private fun VersionHistoryPreview() {
    CBTDiaryTheme {
        VersionHistoryContent(
            state = VersionsState(
                versions = listOf(
                    Conceptualization(
                        id = 3, version = 3, versionNote = "Обновлено после 5 СМЭР записей",
                        coreBeliefs = listOf(),
                        createdAt = System.currentTimeMillis()
                    ),
                    Conceptualization(
                        id = 2, version = 2, versionNote = "Добавлены альтернативы",
                        createdAt = System.currentTimeMillis() - 86400000
                    ),
                    Conceptualization(
                        id = 1, version = 1, versionNote = "Первая версия",
                        createdAt = System.currentTimeMillis() - 172800000
                    )
                ),
                isLoading = false
            ),
            currentVersion = 3,
            onDelete = {},
            onSelect = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, name = "Version History Empty")
@Composable
private fun VersionHistoryEmptyPreview() {
    CBTDiaryTheme {
        VersionHistoryContent(
            state = VersionsState(versions = emptyList(), isLoading = false),
            currentVersion = null,
            onDelete = {},
            onSelect = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

// endregion
