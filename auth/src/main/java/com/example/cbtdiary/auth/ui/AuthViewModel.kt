package com.example.cbtdiary.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.auth.domain.model.AuthMode
import com.example.cbtdiary.auth.domain.usecase.IsPinSetUseCase
import com.example.cbtdiary.auth.domain.usecase.SetPinUseCase
import com.example.cbtdiary.auth.domain.usecase.VerifyPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val mode: AuthMode = AuthMode.UNLOCK,
    val enteredPin: String = "",
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = true,
    val isBiometricAvailable: Boolean = false,
    val title: String = "",
    val subtitle: String = ""
) {
    val filledDots: Int get() = enteredPin.length
}

sealed class AuthEvent {
    data object AuthSuccess : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val isPinSetUseCase: IsPinSetUseCase,
    private val setPinUseCase: SetPinUseCase,
    private val verifyPinUseCase: VerifyPinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var setupPin: String = ""

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val pinSet = isPinSetUseCase()
            val mode = if (pinSet) AuthMode.UNLOCK else AuthMode.SETUP_PIN
            _uiState.update {
                it.copy(
                    mode = mode,
                    isLoading = false,
                    title = getTitleForMode(mode),
                    subtitle = getSubtitleForMode(mode)
                )
            }
        }
    }

    fun setBiometricAvailable(available: Boolean) {
        _uiState.update { it.copy(isBiometricAvailable = available) }
    }

    fun onDigitEntered(digit: Int) {
        val state = _uiState.value
        if (state.enteredPin.length >= 4 || state.isSuccess) return

        val newPin = state.enteredPin + digit.toString()

        _uiState.update {
            it.copy(
                enteredPin = newPin,
                isError = false,
                errorMessage = ""
            )
        }

        if (newPin.length == 4) {
            handlePinComplete(newPin)
        }
    }

    fun onDeleteDigit() {
        val state = _uiState.value
        if (state.enteredPin.isEmpty() || state.isSuccess) return

        _uiState.update {
            it.copy(
                enteredPin = it.enteredPin.dropLast(1),
                isError = false,
                errorMessage = ""
            )
        }
    }

    fun onBiometricSuccess() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSuccess = true) }
            delay(400)
            _events.send(AuthEvent.AuthSuccess)
        }
    }

    private fun handlePinComplete(pin: String) {
        viewModelScope.launch {
            delay(150)

            when (_uiState.value.mode) {
                AuthMode.SETUP_PIN -> {
                    setupPin = pin
                    _uiState.update {
                        it.copy(
                            mode = AuthMode.CONFIRM_PIN,
                            enteredPin = "",
                            title = getTitleForMode(AuthMode.CONFIRM_PIN),
                            subtitle = getSubtitleForMode(AuthMode.CONFIRM_PIN)
                        )
                    }
                }
                AuthMode.CONFIRM_PIN -> {
                    if (pin == setupPin) {
                        try {
                            setPinUseCase(pin)
                            _uiState.update { it.copy(isSuccess = true) }
                            delay(400)
                            _events.send(AuthEvent.AuthSuccess)
                        } catch (e: Exception) {
                            showError("Ошибка сохранения")
                        }
                    } else {
                        showError("Коды не совпадают")
                        delay(800)
                        setupPin = ""
                        _uiState.update {
                            it.copy(
                                mode = AuthMode.SETUP_PIN,
                                enteredPin = "",
                                isError = false,
                                errorMessage = "",
                                title = getTitleForMode(AuthMode.SETUP_PIN),
                                subtitle = getSubtitleForMode(AuthMode.SETUP_PIN)
                            )
                        }
                    }
                }
                AuthMode.UNLOCK -> {
                    val valid = verifyPinUseCase(pin)
                    if (valid) {
                        _uiState.update { it.copy(isSuccess = true) }
                        delay(400)
                        _events.send(AuthEvent.AuthSuccess)
                    } else {
                        showError("Неверный код")
                    }
                }
            }
        }
    }

    private suspend fun showError(message: String) {
        _uiState.update {
            it.copy(
                isError = true,
                errorMessage = message
            )
        }
        delay(600)
        _uiState.update {
            it.copy(
                enteredPin = "",
                isError = false
            )
        }
    }

    private fun getTitleForMode(mode: AuthMode): String = when (mode) {
        AuthMode.SETUP_PIN -> "Создайте код-пароль"
        AuthMode.CONFIRM_PIN -> "Повторите код"
        AuthMode.UNLOCK -> "Введите код-пароль"
    }

    private fun getSubtitleForMode(mode: AuthMode): String = when (mode) {
        AuthMode.SETUP_PIN -> "Придумайте 4-значный код для защиты дневника"
        AuthMode.CONFIRM_PIN -> "Введите код ещё раз для подтверждения"
        AuthMode.UNLOCK -> "Для доступа к дневнику"
    }
}
