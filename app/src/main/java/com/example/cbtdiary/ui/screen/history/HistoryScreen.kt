package com.example.cbtdiary.ui.screen.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.HistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    onNavigateToEntry: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HistoryScreenContent(
        entries = uiState.entries,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onNavigateToEntry = onNavigateToEntry,
        onDeleteEntry = viewModel::deleteEntry,
        onErrorDismissed = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    entries: List<DiaryEntry>,
    isLoading: Boolean,
    error: String?,
    onNavigateToEntry: (Long) -> Unit,
    onDeleteEntry: (DiaryEntry) -> Unit,
    onErrorDismissed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var entryToDelete by remember { mutableStateOf<DiaryEntry?>(null) }
    
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onErrorDismissed()
        }
    }
    
    // Диалог подтверждения удаления
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Удаление записи") },
            text = { Text("Вы уверены, что хотите удалить эту запись? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        entryToDelete?.let(onDeleteEntry)
                        entryToDelete = null
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Дневник КПТ") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEntry(DiaryEntry.NEW_ENTRY_ID) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить запись"
                )
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            entries.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Нет записей",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нажмите + чтобы создать первую запись",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entries) { entry ->
                        EntryCard(
                            entry = entry,
                            onClick = { onNavigateToEntry(entry.id) },
                            onDelete = { entryToDelete = entry }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EntryCard(
    entry: DiaryEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale("ru", "RU"))
    }
    val formattedDate = remember(entry.createdAt) {
        Instant.ofEpochMilli(entry.createdAt)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (entry.whatHappened.isNotBlank()) {
                    Text(
                        text = entry.whatHappened,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (entry.emotions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        entry.emotions.take(3).forEach { emotion ->
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = emotion,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (entry.emotions.size > 3) {
                            Text(
                                text = "+${entry.emotions.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "History Screen - With Entries")
@Composable
fun HistoryScreenPreviewWithEntries() {
    CBTDiaryTheme {
        val sampleEntries = listOf(
            DiaryEntry(
                id = 1L,
                whatHappened = "Партнер сказал, что я не уделяю достаточно внимания",
                feelings = "Чувство обиды, напряжение в плечах",
                whatIWantedToDo = "Начать спорить, оправдываться",
                whatIDidActually = "Попытался объяснить свою точку зрения спокойно",
                emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ"),
                createdAt = System.currentTimeMillis() - 86400000,
                updatedAt = System.currentTimeMillis() - 86400000
            ),
            DiaryEntry(
                id = 2L,
                whatHappened = "Коллега критиковал мою работу на совещании",
                feelings = "Стыд, желание защищаться, напряжение",
                whatIWantedToDo = "Начать спорить, уйти",
                whatIDidActually = "Выслушал критику и задал уточняющие вопросы",
                emotions = listOf("ВИНА/СТЫД", "ТРЕВОГА", "РАЗДРАЖЕНИЕ", "ОБИДА"),
                createdAt = System.currentTimeMillis() - 172800000,
                updatedAt = System.currentTimeMillis() - 172800000
            ),
            DiaryEntry(
                id = 3L,
                whatHappened = "Успешно завершил важный проект",
                feelings = "Гордость, удовлетворение, легкость",
                whatIWantedToDo = "Поделиться радостью с близкими",
                whatIDidActually = "Позвонил родителям и рассказал о успехе",
                emotions = listOf("ГОРДОСТЬ", "СЧАСТЬЕ"),
                createdAt = System.currentTimeMillis() - 259200000,
                updatedAt = System.currentTimeMillis() - 259200000
            )
        )
        
        HistoryScreenContent(
            entries = sampleEntries,
            isLoading = false,
            error = null,
            onNavigateToEntry = {},
            onDeleteEntry = {}
        )
    }
}

@Preview(showBackground = true, name = "History Screen - Empty")
@Composable
fun HistoryScreenPreviewEmpty() {
    CBTDiaryTheme {
        HistoryScreenContent(
            entries = emptyList(),
            isLoading = false,
            error = null,
            onNavigateToEntry = {},
            onDeleteEntry = {}
        )
    }
}

@Preview(showBackground = true, name = "Entry Card")
@Composable
fun EntryCardPreview() {
    CBTDiaryTheme {
        val sampleEntry = DiaryEntry(
            id = 1L,
            whatHappened = "Партнер сказал, что я не уделяю достаточно внимания. Это вызвало у меня сильную реакцию.",
            feelings = "Чувство обиды, напряжение в плечах, желание защищаться",
            whatIWantedToDo = "Начать спорить, оправдываться, уйти",
            whatIDidActually = "Попытался объяснить свою точку зрения спокойно",
            emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ", "УЯЗВЛЕННОСТЬ"),
            createdAt = System.currentTimeMillis() - 86400000,
            updatedAt = System.currentTimeMillis() - 86400000
        )
        
        EntryCard(
            entry = sampleEntry,
            onClick = {},
            onDelete = {}
        )
    }
}
