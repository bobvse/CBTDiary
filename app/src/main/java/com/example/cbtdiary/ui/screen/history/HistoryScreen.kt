package com.example.cbtdiary.ui.screen.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.model.Emotions
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.HistoryUiState
import com.example.cbtdiary.ui.viewmodel.HistoryViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HistoryScreen(
    onNavigateToNewEntry: () -> Unit,
    onNavigateToViewEntry: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HistoryScreenContent(
        uiState = uiState,
        onNavigateToNewEntry = onNavigateToNewEntry,
        onNavigateToViewEntry = onNavigateToViewEntry,
        onNavigateToEdit = onNavigateToEdit,
        onSelectDate = viewModel::selectDate,
        onPreviousMonth = viewModel::goToPreviousMonth,
        onNextMonth = viewModel::goToNextMonth,
        onErrorDismissed = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    uiState: HistoryUiState,
    onNavigateToNewEntry: () -> Unit,
    onNavigateToViewEntry: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit = {},
    onSelectDate: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onErrorDismissed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onErrorDismissed()
        }
    }

    selectedEntry?.let { entry ->
        EntryDetailBottomSheet(
            entry = entry,
            onDismiss = { selectedEntry = null },
            onNavigateToView = { onNavigateToViewEntry(entry.id) },
            onNavigateToEdit = { onNavigateToEdit(entry.id) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.history_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewEntry,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_entry)
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                item {
                    CalendarWidget(
                        currentMonth = uiState.currentMonth,
                        selectedDate = uiState.selectedDate,
                        datesWithEntries = uiState.datesWithEntries,
                        onDateSelected = onSelectDate,
                        onPreviousMonth = onPreviousMonth,
                        onNextMonth = onNextMonth,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    val selectedDateFormatted = remember(uiState.selectedDate) {
                        val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru-RU"))
                        uiState.selectedDate.format(formatter)
                    }
                    val count = uiState.selectedDateEntries.size

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDateFormatted,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (count > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (uiState.selectedDateEntries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.history_no_entries),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.history_tap_to_add),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                } else {
                    items(
                        uiState.selectedDateEntries,
                        key = { it.id }
                    ) { entry ->
                        EntryCard(
                            entry = entry,
                            onClick = { selectedEntry = entry },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarWidget(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    datesWithEntries: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = Locale.forLanguageTag("ru-RU")
    val monthName = remember(currentMonth) {
        currentMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { it.titlecase(locale) }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = stringResource(R.string.cd_previous_month),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "$monthName ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = stringResource(R.string.cd_next_month),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val daysOfWeek = listOf(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                )
                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, locale).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val firstDayOfMonth = currentMonth.atDay(1)
            val startDayOfWeek = firstDayOfMonth.dayOfWeek
            val daysOffset = (startDayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
            val totalDays = currentMonth.lengthOfMonth()
            val totalCells = daysOffset + totalDays
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - daysOffset + 1
                        if (dayNumber in 1..totalDays) {
                            val date = currentMonth.atDay(dayNumber)
                            val isSelected = date == selectedDate
                            val isToday = date == LocalDate.now()
                            val hasEntries = datesWithEntries.contains(date)

                            CalendarDay(
                                day = dayNumber,
                                isSelected = isSelected,
                                isToday = isToday,
                                hasEntries = hasEntries,
                                onClick = { onDateSelected(date) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasEntries: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dayBg"
    )
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dayText"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 14.sp
            )
            if (hasEntries && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            } else if (hasEntries && isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}

@Composable
fun EntryCard(
    entry: DiaryEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("ru-RU"))
    }
    val formattedTime = remember(entry.createdAt) {
        Instant.ofEpochMilli(entry.createdAt)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (entry.emotions.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${entry.emotions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (entry.situation.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = entry.situation,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (entry.emotions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    entry.emotions.take(3).forEach { emotionName ->
                        val emotion = Emotions.allEmotions.find { it.name == emotionName }
                        val chipColor = if (emotion != null) Color(emotion.category.color)
                        else MaterialTheme.colorScheme.primary
                        Surface(
                            color = chipColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = emotionName,
                                style = MaterialTheme.typography.labelSmall,
                                color = chipColor,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (entry.emotions.size > 3) {
                        Text(
                            text = "+${entry.emotions.size - 3}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDetailBottomSheet(
    entry: DiaryEntry,
    onDismiss: () -> Unit,
    onNavigateToView: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val formatter = remember {
        DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru-RU"))
    }
    val formattedDate = remember(entry.createdAt) {
        Instant.ofEpochMilli(entry.createdAt)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (entry.situation.isNotBlank()) {
                DetailSection(
                    title = stringResource(R.string.section_situation),
                    content = entry.situation,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (entry.thoughts.isNotBlank()) {
                DetailSection(
                    title = stringResource(R.string.section_thoughts),
                    content = entry.thoughts,
                    color = Color(0xFF9C27B0)
                )
            }

            if (entry.emotions.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.section_emotions),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    entry.emotions.forEach { emotionName ->
                        val emotion = Emotions.allEmotions.find { it.name == emotionName }
                        val chipColor = if (emotion != null) Color(emotion.category.color)
                        else MaterialTheme.colorScheme.primary
                        Surface(
                            color = chipColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = emotionName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = chipColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (entry.bodyReaction.isNotBlank()) {
                DetailSection(
                    title = stringResource(R.string.section_body_reaction),
                    content = entry.bodyReaction,
                    color = Color(0xFFFF9800)
                )
            }

            if (entry.actionReaction.isNotBlank()) {
                DetailSection(
                    title = stringResource(R.string.section_action_reaction),
                    content = entry.actionReaction,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onNavigateToEdit()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.history_detail_edit))
                }
                FilledTonalButton(
                    onClick = {
                        onDismiss()
                        onNavigateToView()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.history_detail_more))
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
    color: Color
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Preview(showBackground = true, name = "History Screen - With Entries")
@Composable
private fun HistoryScreenPreview() {
    CBTDiaryTheme {
        HistoryScreenContent(
            uiState = HistoryUiState(
                entries = listOf(
                    DiaryEntry(
                        id = 1L,
                        situation = "Партнер сказал обидные слова",
                        thoughts = "Я не достаточно хорош",
                        emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ"),
                        bodyReaction = "Напряжение в плечах",
                        actionReaction = "Попытался объяснить свою позицию",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            ),
            onNavigateToNewEntry = {},
            onNavigateToViewEntry = {},
            onNavigateToEdit = {},
            onSelectDate = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onErrorDismissed = {}
        )
    }
}

@Preview(showBackground = true, name = "History Screen - Empty")
@Composable
private fun HistoryScreenEmptyPreview() {
    CBTDiaryTheme {
        HistoryScreenContent(
            uiState = HistoryUiState(),
            onNavigateToNewEntry = {},
            onNavigateToViewEntry = {},
            onNavigateToEdit = {},
            onSelectDate = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onErrorDismissed = {}
        )
    }
}

@Preview(showBackground = true, name = "Entry Card")
@Composable
private fun EntryCardPreview() {
    CBTDiaryTheme {
        EntryCard(
            entry = DiaryEntry(
                id = 1L,
                situation = "Коллега критиковал мою работу на совещании",
                thoughts = "Я плохо справляюсь",
                emotions = listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ", "ЗЛОСТЬ"),
                bodyReaction = "Учащённое сердцебиение",
                actionReaction = "Выслушал и задал вопросы",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            onClick = {}
        )
    }
}
