package com.example.cbtdiary.copingcards.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.copingcards.R
import com.example.cbtdiary.copingcards.domain.model.CopingCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopingCardQuizScreen(
    viewModel: CopingCardsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.quizState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quiz_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        when {
            state.cards.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.quiz_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
            state.isComplete -> {
                QuizCompleteView(
                    correct = state.correctCount,
                    total = state.totalAnswered,
                    onRestart = viewModel::startQuiz,
                    onFinish = onNavigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                QuizCardView(
                    card = state.currentCard!!,
                    isRevealed = state.isRevealed,
                    progress = state.progress,
                    progressText = stringResource(R.string.quiz_progress, state.currentIndex + 1, state.cards.size),
                    onReveal = viewModel::revealAnswer,
                    onKnewIt = { viewModel.answerQuiz(true) },
                    onDidntKnow = { viewModel.answerQuiz(false) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun QuizCardView(
    card: CopingCard,
    isRevealed: Boolean,
    progress: Float,
    progressText: String,
    onReveal: () -> Unit,
    onKnewIt: () -> Unit,
    onDidntKnow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = progressText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = isRevealed,
            transitionSpec = {
                (fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                        scaleIn(initialScale = 0.95f, animationSpec = spring(stiffness = Spring.StiffnessMedium)))
                    .togetherWith(fadeOut(spring(stiffness = Spring.StiffnessMedium)))
            },
            modifier = Modifier.weight(1f),
            label = "quizFlip"
        ) { revealed ->
            if (!revealed) {
                FlipCardContent(
                    card = card.copy(backText = ""),
                    onUse = {},
                    showUseButton = false,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                FlipCardContent(
                    card = card,
                    onUse = {},
                    showUseButton = false,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isRevealed) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.quiz_instruction),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onReveal,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.quiz_reveal))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDidntKnow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.quiz_didnt_know))
                }
                FilledTonalButton(
                    onClick = onKnewIt,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.quiz_knew_it))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun QuizCompleteView(
    correct: Int,
    total: Int,
    onRestart: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (total > 0) (correct * 100 / total) else 0
    val resultColor = when {
        percentage >= 80 -> Color(0xFF43A047)
        percentage >= 50 -> Color(0xFFFFC107)
        else -> Color(0xFFE53935)
    }

    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            resultColor.copy(alpha = 0.15f),
                            resultColor.copy(alpha = 0.03f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = resultColor
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.quiz_complete_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.quiz_complete_score, correct, total),
            style = MaterialTheme.typography.titleLarge,
            color = resultColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onRestart,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.quiz_restart))
            }
            FilledTonalButton(
                onClick = onFinish,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.quiz_finish))
            }
        }
    }
}

// region Previews

@Preview(showBackground = true, name = "Quiz Card - Not Revealed")
@Composable
private fun QuizCardNotRevealedPreview() {
    MaterialTheme {
        QuizCardView(
            card = CopingCard(
                frontText = "Все думают, что я неудачник",
                backText = "Я не могу знать мысли других людей",
                strategies = listOf("Факт vs Страх")
            ),
            isRevealed = false,
            progress = 0.3f,
            progressText = "2 / 7",
            onReveal = {},
            onKnewIt = {},
            onDidntKnow = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, name = "Quiz Complete")
@Composable
private fun QuizCompletePreview() {
    MaterialTheme {
        QuizCompleteView(
            correct = 5,
            total = 7,
            onRestart = {},
            onFinish = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

// endregion
