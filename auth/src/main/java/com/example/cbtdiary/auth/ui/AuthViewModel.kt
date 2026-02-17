package com.example.cbtdiary.auth.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.auth.R
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
    @StringRes val errorMessageRes: Int = 0,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = true,
    val isBiometricAvailable: Boolean = false,
    @StringRes val titleRes: Int = 0,
    @StringRes val subtitleRes: Int = 0
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
                    titleRes = getTitleResForMode(mode),
                    subtitleRes = getSubtitleResForMode(mode)
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
                errorMessageRes = 0
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
                errorMessageRes = 0
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
                            titleRes = getTitleResForMode(AuthMode.CONFIRM_PIN),
                            subtitleRes = getSubtitleResForMode(AuthMode.CONFIRM_PIN)
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
                            showError(R.string.auth_error_save)
                        }
                    } else {
                        showError(R.string.auth_error_mismatch)
                        delay(800)
                        setupPin = ""
                        _uiState.update {
                            it.copy(
                                mode = AuthMode.SETUP_PIN,
                                enteredPin = "",
                                isError = false,
                                errorMessageRes = 0,
                                titleRes = getTitleResForMode(AuthMode.SETUP_PIN),
                                subtitleRes = getSubtitleResForMode(AuthMode.SETUP_PIN)
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
                        showError(R.string.auth_error_wrong)
                    }
                }
            }
        }
    }

    private suspend fun showError(@StringRes messageRes: Int) {
        _uiState.update {
            it.copy(
                isError = true,
                errorMessageRes = messageRes
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

    @StringRes
    private fun getTitleResForMode(mode: AuthMode): Int = when (mode) {
        AuthMode.SETUP_PIN -> R.string.auth_title_setup
        AuthMode.CONFIRM_PIN -> R.string.auth_title_confirm
        AuthMode.UNLOCK -> R.string.auth_title_unlock
    }

    @StringRes
    private fun getSubtitleResForMode(mode: AuthMode): Int = when (mode) {
        AuthMode.SETUP_PIN -> R.string.auth_subtitle_setup
        AuthMode.CONFIRM_PIN -> R.string.auth_subtitle_confirm
        AuthMode.UNLOCK -> R.string.auth_subtitle_unlock
    }
}
