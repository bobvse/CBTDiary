package com.example.cbtdiary.ui.screen.entry

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.R
import com.example.cbtdiary.ui.components.EmotionSelector
import com.example.cbtdiary.ui.theme.CBTDiaryTheme
import com.example.cbtdiary.ui.viewmodel.EntryEvent
import com.example.cbtdiary.ui.viewmodel.EntryStep
import com.example.cbtdiary.ui.viewmodel.EntryUiState
import com.example.cbtdiary.ui.viewmodel.EntryViewModel
import kotlinx.coroutines.delay

private fun capitalizeFirstChar(text: String): String {
    if (text.isEmpty()) return text
    val first = text[0]
    if (first.isLowerCase()) {
        return first.titlecase() + text.substring(1)
    }
    return text
}

@Composable
fun EntryScreen(
    onNavigateBack: () -> Unit,
    entryId: Long? = null,
    initialDate: Long? = null,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entryId) {
        entryId?.let { viewModel.loadEntry(it) }
    }

    LaunchedEffect(initialDate) {
        initialDate?.let { viewModel.updateSelectedDate(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EntryEvent.SaveSuccess -> onNavigateBack()
            }
        }
    }

    BackHandler {
        if (uiState.currentStep != EntryStep.SITUATION) {
            viewModel.goToPreviousStep()
        } else {
            onNavigateBack()
        }
    }

    EntryScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNextStep = viewModel::goToNextStep,
        onPreviousStep = viewModel::goToPreviousStep,
        onSituationChange = viewModel::updateSituation,
        onThoughtsChange = viewModel::updateThoughts,
        onEmotionToggle = viewModel::toggleEmotion,
        onBodyReactionChange = viewModel::updateBodyReaction,
        onActionReactionChange = viewModel::updateActionReaction,
        onSave = viewModel::saveEntry,
        onErrorDismissed = viewModel::clearError,
        onDateChange = viewModel::updateSelectedDate
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EntryScreenContent(
    uiState: EntryUiState,
    onNavigateBack: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSituationChange: (String) -> Unit,
    onThoughtsChange: (String) -> Unit,
    onEmotionToggle: (String) -> Unit,
    onBodyReactionChange: (String) -> Unit,
    onActionReactionChange: (String) -> Unit,
    onSave: () -> Unit,
    onErrorDismissed: () -> Unit,
    onDateChange: (Long) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(uiState.errorRes) {
        uiState.errorRes?.let {
            snackbarHostState.showSnackbar(context.getString(it))
            onErrorDismissed()
        }
    }

    // Proactively manage keyboard visibility based on current step
    LaunchedEffect(uiState.currentStep) {
        if (!uiState.currentStep.isTextStep) {
            delay(50)
            keyboardController?.hide()
        }
    }

    val progress by animateFloatAsState(
        targetValue = (uiState.currentStep.index + 1).toFloat() / EntryStep.totalSteps,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep != EntryStep.SITUATION) {
                            onPreviousStep()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = if (uiState.currentStep == EntryStep.SITUATION)
                                Icons.Default.Close
                            else
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    Text(
                        text = stringResource(
                            R.string.step_counter,
                            uiState.currentStep.index + 1,
                            EntryStep.totalSteps
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            StepIndicator(
                currentStep = uiState.currentStep,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            if (uiState.currentStep == EntryStep.SITUATION) {
                DatePickerChip(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = onDateChange,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    val direction = uiState.direction
                    (slideInHorizontally(
                        initialOffsetX = { fullWidth -> direction * fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(animationSpec = tween(300)))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -direction * fullWidth },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) + fadeOut(animationSpec = tween(200))
                        )
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "stepContent"
            ) { step ->
                when (step) {
                    EntryStep.SITUATION -> TextStepContent(
                        value = uiState.situation,
                        onValueChange = onSituationChange,
                        hint = stringResource(R.string.entry_hint_situation),
                        modifier = Modifier.fillMaxSize()
                    )

                    EntryStep.THOUGHTS -> TextStepContent(
                        value = uiState.thoughts,
                        onValueChange = onThoughtsChange,
                        hint = stringResource(R.string.entry_hint_thoughts),
                        modifier = Modifier.fillMaxSize()
                    )

                    EntryStep.EMOTION -> EmotionStepContent(
                        selectedEmotions = uiState.emotions,
                        onEmotionToggle = onEmotionToggle,
                        modifier = Modifier.fillMaxSize()
                    )

                    EntryStep.BODY_REACTION -> TextStepContent(
                        value = uiState.bodyReaction,
                        onValueChange = onBodyReactionChange,
                        hint = stringResource(R.string.entry_hint_body_reaction),
                        modifier = Modifier.fillMaxSize()
                    )

                    EntryStep.ACTION_REACTION -> TextStepContent(
                        value = uiState.actionReaction,
                        onValueChange = onActionReactionChange,
                        hint = stringResource(R.string.entry_hint_action_reaction),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            BottomNavigation(
                currentStep = uiState.currentStep,
                isSaving = uiState.isSaving,
                onPrevious = onPreviousStep,
                onNext = onNextStep,
                onSave = onSave,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: EntryStep,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(currentStep.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(currentStep.subtitleRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TextStepContent(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(150)
        try {
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (_: Exception) {
            // FocusRequester may not be attached yet in rare cases
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = value.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { newText ->
                onValueChange(capitalizeFirstChar(newText))
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.bodyLarge,
            minLines = 6,
            maxLines = 12,
            placeholder = {
                Text(
                    stringResource(R.string.entry_placeholder),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EmotionStepContent(
    selectedEmotions: List<String>,
    onEmotionToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        keyboardController?.hide()
    }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (selectedEmotions.isNotEmpty()) {
            Text(
                text = stringResource(R.string.entry_emotions_selected, selectedEmotions.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.entry_emotions_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        EmotionSelector(
            selectedEmotions = selectedEmotions,
            onEmotionToggle = onEmotionToggle
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DatePickerChip(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateText = remember(selectedDate) {
        val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.forLanguageTag("ru-RU"))
        sdf.format(java.util.Date(selectedDate))
    }

    Surface(
        modifier = modifier.clickable {
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val cal = java.util.Calendar.getInstance()
                    cal.set(year, month, dayOfMonth)
                    val origCal = java.util.Calendar.getInstance()
                    origCal.timeInMillis = selectedDate
                    cal.set(java.util.Calendar.HOUR_OF_DAY, origCal.get(java.util.Calendar.HOUR_OF_DAY))
                    cal.set(java.util.Calendar.MINUTE, origCal.get(java.util.Calendar.MINUTE))
                    onDateSelected(cal.timeInMillis)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.CalendarToday,
                contentDescription = stringResource(R.string.cd_select_date),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    currentStep: EntryStep,
    isSaving: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep != EntryStep.SITUATION) {
            FilledTonalButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.action_back))
            }
        }

        if (currentStep == EntryStep.ACTION_REACTION) {
            Button(
                onClick = { if (!isSaving) onSave() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.action_saving))
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.action_save))
                }
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.action_next))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Entry Screen - Situation Step")
@Composable
private fun EntryScreenPreview() {
    CBTDiaryTheme {
        EntryScreenContent(
            uiState = EntryUiState(
                situation = "Партнер сказал обидные слова",
                currentStep = EntryStep.SITUATION
            ),
            onNavigateBack = {},
            onNextStep = {},
            onPreviousStep = {},
            onSituationChange = {},
            onThoughtsChange = {},
            onEmotionToggle = {},
            onBodyReactionChange = {},
            onActionReactionChange = {},
            onSave = {},
            onErrorDismissed = {},
            onDateChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Entry Screen - Emotion Step")
@Composable
private fun EntryScreenEmotionPreview() {
    CBTDiaryTheme {
        EntryScreenContent(
            uiState = EntryUiState(
                emotions = listOf("ОБИДА", "ТРЕВОГА"),
                currentStep = EntryStep.EMOTION
            ),
            onNavigateBack = {},
            onNextStep = {},
            onPreviousStep = {},
            onSituationChange = {},
            onThoughtsChange = {},
            onEmotionToggle = {},
            onBodyReactionChange = {},
            onActionReactionChange = {},
            onSave = {},
            onErrorDismissed = {},
            onDateChange = {}
        )
    }
}
