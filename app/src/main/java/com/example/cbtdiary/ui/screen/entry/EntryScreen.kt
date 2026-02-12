package com.example.cbtdiary.ui.screen.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.cbtdiary.ui.viewmodel.EntryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == 0L) "Новая запись" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.saveEntry(onNavigateBack)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Показываем временную метку для редактирования
            if (entryId != 0L && uiState.entry.createdAt > 0) {
                val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru", "RU"))
                val formattedDate = dateFormat.format(Date(uiState.entry.createdAt))
                Text(
                    text = "Создано: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            OutlinedTextField(
                value = uiState.entry.whatHappened,
                onValueChange = viewModel::updateWhatHappened,
                label = { Text("Что произошло? (Событие, слова партнера)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.entry.feelings,
                onValueChange = viewModel::updateFeelings,
                label = { Text("Какие чувства/телесные ощущения возникли?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.entry.whatIWantedToDo,
                onValueChange = viewModel::updateWhatIWantedToDo,
                label = { Text("Что я ХОТЕЛ(А) сделать в этот момент?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.entry.whatIDidActually,
                onValueChange = viewModel::updateWhatIDidActually,
                label = { Text("Что я сделал(а) НА САМОМ ДЕЛЕ?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Показываем выбранные эмоции отдельно
            if (uiState.entry.emotions.isNotEmpty()) {
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
                    uiState.entry.emotions.forEach { emotionName ->
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
                selectedEmotions = uiState.entry.emotions,
                onEmotionToggle = viewModel::toggleEmotion
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Entry Screen - New")
@Composable
fun EntryScreenPreviewNew() {
    CBTDiaryTheme {
        var whatHappened by remember { mutableStateOf("") }
        var feelings by remember { mutableStateOf("") }
        var whatIWantedToDo by remember { mutableStateOf("") }
        var whatIDidActually by remember { mutableStateOf("") }
        var selectedEmotions by remember { mutableStateOf<List<String>>(emptyList()) }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Новая запись") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = whatHappened,
                    onValueChange = { whatHappened = it },
                    label = { Text("Что произошло? (Событие, слова партнера)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = feelings,
                    onValueChange = { feelings = it },
                    label = { Text("Какие чувства/телесные ощущения возникли?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = whatIWantedToDo,
                    onValueChange = { whatIWantedToDo = it },
                    label = { Text("Что я ХОТЕЛ(А) сделать в этот момент?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = whatIDidActually,
                    onValueChange = { whatIDidActually = it },
                    label = { Text("Что я сделал(а) НА САМОМ ДЕЛЕ?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Эмоции",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                EmotionSelector(
                    selectedEmotions = selectedEmotions,
                    onEmotionToggle = { emotion ->
                        selectedEmotions = if (selectedEmotions.contains(emotion)) {
                            selectedEmotions - emotion
                        } else {
                            selectedEmotions + emotion
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Entry Screen - Edit")
@Composable
fun EntryScreenPreviewEdit() {
    CBTDiaryTheme {
        val sampleEntry = DiaryEntry(
            id = 1L,
            whatHappened = "Партнер сказал, что я не уделяю достаточно внимания",
            feelings = "Чувство обиды, напряжение в плечах, желание защищаться",
            whatIWantedToDo = "Начать спорить, оправдываться, уйти",
            whatIDidActually = "Попытался объяснить свою точку зрения спокойно",
            emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ"),
            createdAt = System.currentTimeMillis() - 86400000,
            updatedAt = System.currentTimeMillis() - 86400000
        )
        
        var whatHappened by remember { mutableStateOf(sampleEntry.whatHappened) }
        var feelings by remember { mutableStateOf(sampleEntry.feelings) }
        var whatIWantedToDo by remember { mutableStateOf(sampleEntry.whatIWantedToDo) }
        var whatIDidActually by remember { mutableStateOf(sampleEntry.whatIDidActually) }
        var selectedEmotions by remember { mutableStateOf(sampleEntry.emotions) }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Редактирование") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Сохранить"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = whatHappened,
                    onValueChange = { whatHappened = it },
                    label = { Text("Что произошло? (Событие, слова партнера)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = feelings,
                    onValueChange = { feelings = it },
                    label = { Text("Какие чувства/телесные ощущения возникли?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = whatIWantedToDo,
                    onValueChange = { whatIWantedToDo = it },
                    label = { Text("Что я ХОТЕЛ(А) сделать в этот момент?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = whatIDidActually,
                    onValueChange = { whatIDidActually = it },
                    label = { Text("Что я сделал(а) НА САМОМ ДЕЛЕ?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Эмоции",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                EmotionSelector(
                    selectedEmotions = selectedEmotions,
                    onEmotionToggle = { emotion ->
                        selectedEmotions = if (selectedEmotions.contains(emotion)) {
                            selectedEmotions - emotion
                        } else {
                            selectedEmotions + emotion
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
