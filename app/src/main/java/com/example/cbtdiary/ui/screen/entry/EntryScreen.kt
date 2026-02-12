package com.example.cbtdiary.ui.screen.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.model.Emotions
import com.example.cbtdiary.ui.components.EmotionSelector
import com.example.cbtdiary.ui.components.SimpleFlowRow
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.EntryEvent
import com.example.cbtdiary.ui.viewmodel.EntryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EntryScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(entryId) {
        viewModel.loadEntry(entryId)
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EntryEvent.SaveSuccess -> onNavigateBack()
            }
        }
    }
    
    EntryScreenContent(
        entry = uiState.entry,
        isNewEntry = entryId == DiaryEntry.NEW_ENTRY_ID,
        isSaving = uiState.isSaving,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onNavigateBack = onNavigateBack,
        onSave = viewModel::saveEntry,
        onWhatHappenedChange = viewModel::updateWhatHappened,
        onFeelingsChange = viewModel::updateFeelings,
        onWhatIWantedToDoChange = viewModel::updateWhatIWantedToDo,
        onWhatIDidActuallyChange = viewModel::updateWhatIDidActually,
        onEmotionToggle = viewModel::toggleEmotion,
        onErrorDismissed = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreenContent(
    entry: DiaryEntry,
    isNewEntry: Boolean,
    isSaving: Boolean,
    isLoading: Boolean,
    error: String?,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onWhatHappenedChange: (String) -> Unit,
    onFeelingsChange: (String) -> Unit,
    onWhatIWantedToDoChange: (String) -> Unit,
    onWhatIDidActuallyChange: (String) -> Unit,
    onEmotionToggle: (String) -> Unit,
    onErrorDismissed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onErrorDismissed()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isNewEntry) "Новая запись" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (!isSaving) onSave() }) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить"
                    )
                }
            }
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                if (!isNewEntry && entry.createdAt > 0) {
                    val formatter = remember {
                        DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale("ru", "RU"))
                    }
                    val formattedDate = remember(entry.createdAt) {
                        Instant.ofEpochMilli(entry.createdAt)
                            .atZone(ZoneId.systemDefault())
                            .format(formatter)
                    }
                    Text(
                        text = "Создано: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                OutlinedTextField(
                    value = entry.whatHappened,
                    onValueChange = onWhatHappenedChange,
                    label = { Text("Что произошло? (Событие, слова партнера)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = entry.feelings,
                    onValueChange = onFeelingsChange,
                    label = { Text("Какие чувства/телесные ощущения возникли?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = entry.whatIWantedToDo,
                    onValueChange = onWhatIWantedToDoChange,
                    label = { Text("Что я ХОТЕЛ(А) сделать в этот момент?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = entry.whatIDidActually,
                    onValueChange = onWhatIDidActuallyChange,
                    label = { Text("Что я сделал(а) НА САМОМ ДЕЛЕ?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (entry.emotions.isNotEmpty()) {
                    Text(
                        text = "Выбранные эмоции",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SimpleFlowRow(
                        horizontalSpacing = 8.dp,
                        verticalSpacing = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        entry.emotions.forEach { emotionName ->
                            val emotion = Emotions.allEmotions.find { it.name == emotionName }
                            if (emotion != null) {
                                Surface(
                                    color = Color(emotion.category.color),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = emotion.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text(
                    text = "Выберите эмоции",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                EmotionSelector(
                    selectedEmotions = entry.emotions,
                    onEmotionToggle = onEmotionToggle
                )
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Entry Screen - New")
@Composable
fun EntryScreenPreviewNew() {
    CBTDiaryTheme {
        var entry by remember {
            mutableStateOf(
                DiaryEntry(
                    whatHappened = "",
                    feelings = "",
                    whatIWantedToDo = "",
                    whatIDidActually = "",
                    emotions = emptyList()
                )
            )
        }
        
        EntryScreenContent(
            entry = entry,
            isNewEntry = true,
            isSaving = false,
            isLoading = false,
            error = null,
            onNavigateBack = {},
            onSave = {},
            onWhatHappenedChange = { entry = entry.copy(whatHappened = it) },
            onFeelingsChange = { entry = entry.copy(feelings = it) },
            onWhatIWantedToDoChange = { entry = entry.copy(whatIWantedToDo = it) },
            onWhatIDidActuallyChange = { entry = entry.copy(whatIDidActually = it) },
            onEmotionToggle = { emotion ->
                entry = entry.copy(
                    emotions = if (entry.emotions.contains(emotion)) {
                        entry.emotions - emotion
                    } else {
                        entry.emotions + emotion
                    }
                )
            }
        )
    }
}

@Preview(showBackground = true, name = "Entry Screen - Edit")
@Composable
fun EntryScreenPreviewEdit() {
    CBTDiaryTheme {
        var entry by remember {
            mutableStateOf(
                DiaryEntry(
                    id = 1L,
                    whatHappened = "Партнер сказал, что я не уделяю достаточно внимания",
                    feelings = "Чувство обиды, напряжение в плечах, желание защищаться",
                    whatIWantedToDo = "Начать спорить, оправдываться, уйти",
                    whatIDidActually = "Попытался объяснить свою точку зрения спокойно",
                    emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ"),
                    createdAt = System.currentTimeMillis() - 86400000,
                    updatedAt = System.currentTimeMillis() - 86400000
                )
            )
        }
        
        EntryScreenContent(
            entry = entry,
            isNewEntry = false,
            isSaving = false,
            isLoading = false,
            error = null,
            onNavigateBack = {},
            onSave = {},
            onWhatHappenedChange = { entry = entry.copy(whatHappened = it) },
            onFeelingsChange = { entry = entry.copy(feelings = it) },
            onWhatIWantedToDoChange = { entry = entry.copy(whatIWantedToDo = it) },
            onWhatIDidActuallyChange = { entry = entry.copy(whatIDidActually = it) },
            onEmotionToggle = { emotion ->
                entry = entry.copy(
                    emotions = if (entry.emotions.contains(emotion)) {
                        entry.emotions - emotion
                    } else {
                        entry.emotions + emotion
                    }
                )
            }
        )
    }
}
