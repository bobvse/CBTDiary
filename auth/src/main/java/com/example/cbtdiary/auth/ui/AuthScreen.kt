package com.example.cbtdiary.auth.ui

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cbtdiary.auth.domain.model.AuthMode
import kotlin.math.roundToInt

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
        viewModel.setBiometricAvailable(canAuthenticate)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.AuthSuccess -> onAuthSuccess()
            }
        }
    }

    // Auto-trigger biometric on unlock mode
    val showBiometric = uiState.mode == AuthMode.UNLOCK
            && uiState.isBiometricAvailable
            && !uiState.isSuccess
    LaunchedEffect(showBiometric) {
        if (showBiometric && uiState.enteredPin.isEmpty()) {
            showBiometricPrompt(context, viewModel)
        }
    }

    if (!uiState.isLoading) {
        AuthScreenContent(
            uiState = uiState,
            onDigitClick = viewModel::onDigitEntered,
            onDeleteClick = viewModel::onDeleteDigit,
            onBiometricClick = {
                showBiometricPrompt(context, viewModel)
            }
        )
    }
}

private fun showBiometricPrompt(
    context: android.content.Context,
    viewModel: AuthViewModel
) {
    val activity = context as? FragmentActivity ?: return

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Вход в дневник")
        .setSubtitle("Используйте отпечаток пальца")
        .setNegativeButtonText("Использовать код")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()

    val biometricPrompt = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                viewModel.onBiometricSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {}
            override fun onAuthenticationFailed() {}
        }
    )

    biometricPrompt.authenticate(promptInfo)
}

@Composable
private fun AuthScreenContent(
    uiState: AuthUiState,
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: () -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            for (i in 0 until 4) {
                shakeOffset.animateTo(
                    targetValue = if (i % 2 == 0) 12f else -12f,
                    animationSpec = tween(durationMillis = 60)
                )
            }
            shakeOffset.animateTo(0f, animationSpec = tween(60))
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lock icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isSuccess) "✓" else "🔒",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = uiState.title,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    },
                    label = "title"
                ) { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = uiState.subtitle,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    },
                    label = "subtitle"
                ) { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // PIN dots
                Row(
                    modifier = Modifier.offset {
                        IntOffset(shakeOffset.value.roundToInt(), 0)
                    },
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    repeat(4) { index ->
                        PinDot(
                            isFilled = index < uiState.filledDots,
                            isError = uiState.isError,
                            isSuccess = uiState.isSuccess,
                            index = index
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                AnimatedContent(
                    targetState = uiState.errorMessage,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(100))
                    },
                    label = "error"
                ) { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            // Number pad
            NumberPad(
                onDigitClick = onDigitClick,
                onDeleteClick = onDeleteClick,
                onBiometricClick = onBiometricClick,
                showBiometric = uiState.isBiometricAvailable && uiState.mode == AuthMode.UNLOCK,
                enabled = !uiState.isSuccess,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
    }
}

@Composable
private fun PinDot(
    isFilled: Boolean,
    isError: Boolean,
    isSuccess: Boolean,
    index: Int
) {
    val scale by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dotScale$index"
    )

    val color by animateColorAsState(
        targetValue = when {
            isSuccess -> Color(0xFF4CAF50)
            isError -> MaterialTheme.colorScheme.error
            isFilled -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dotColor$index"
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun NumberPad(
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: () -> Unit,
    showBiometric: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: 1 2 3
        NumberRow(
            digits = listOf(1, 2, 3),
            onDigitClick = onDigitClick,
            enabled = enabled
        )
        // Row 2: 4 5 6
        NumberRow(
            digits = listOf(4, 5, 6),
            onDigitClick = onDigitClick,
            enabled = enabled
        )
        // Row 3: 7 8 9
        NumberRow(
            digits = listOf(7, 8, 9),
            onDigitClick = onDigitClick,
            enabled = enabled
        )
        // Row 4: biometric/empty, 0, delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBiometric) {
                ActionButton(
                    icon = Icons.Default.Fingerprint,
                    contentDescription = "Отпечаток пальца",
                    onClick = onBiometricClick,
                    enabled = enabled,
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Spacer(modifier = Modifier.size(72.dp))
            }

            NumberButton(
                digit = 0,
                onClick = { onDigitClick(0) },
                enabled = enabled
            )

            ActionButton(
                icon = Icons.Default.Backspace,
                contentDescription = "Удалить",
                onClick = onDeleteClick,
                enabled = enabled,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NumberRow(
    digits: List<Int>,
    onDigitClick: (Int) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        digits.forEach { digit ->
            NumberButton(
                digit = digit,
                onClick = { onDigitClick(digit) },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun NumberButton(
    digit: Int,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp),
            tint = tint
        )
    }
}
