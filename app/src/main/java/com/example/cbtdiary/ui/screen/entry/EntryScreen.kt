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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.ui.components.EmotionSelector
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
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        onErrorDismissed = viewModel::clearError
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
    onErrorDismissed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
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
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    Text(
                        text = "${uiState.currentStep.index + 1} / ${EntryStep.totalSteps}",
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
                        hint = "Опишите ситуацию, которая произошла. Что случилось? Где вы были? Кто участвовал?",
                        modifier = Modifier.fillMaxSize()
                    )
                    EntryStep.THOUGHTS -> TextStepContent(
                        value = uiState.thoughts,
                        onValueChange = onThoughtsChange,
                        hint = "Какие мысли пришли в голову? О чём вы подумали в тот момент?",
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
                        hint = "Что вы почувствовали в теле? Напряжение, дрожь, учащённое сердцебиение, ком в горле?",
                        modifier = Modifier.fillMaxSize()
                    )
                    EntryStep.ACTION_REACTION -> TextStepContent(
                        value = uiState.actionReaction,
                        onValueChange = onActionReactionChange,
                        hint = "Что вы сделали? Как отреагировали? Какие действия предприняли?",
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
            text = currentStep.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = currentStep.subtitle,
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
                    "Начните писать...",
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
                text = "Выбрано: ${selectedEmotions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        } else {
            Text(
                text = "Выберите одну или несколько эмоций",
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
                Text("Назад")
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
                    Text("Сохранение...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сохранить")
                }
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Далее")
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
