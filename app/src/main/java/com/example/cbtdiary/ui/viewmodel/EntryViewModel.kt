package com.example.cbtdiary.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.usecase.SaveEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EntryStep(
    val index: Int,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val isTextStep: Boolean
) {
    SITUATION(0, R.string.step_situation_title, R.string.step_situation_subtitle, true),
    THOUGHTS(1, R.string.step_thoughts_title, R.string.step_thoughts_subtitle, true),
    EMOTION(2, R.string.step_emotion_title, R.string.step_emotion_subtitle, false),
    BODY_REACTION(3, R.string.step_body_reaction_title, R.string.step_body_reaction_subtitle, true),
    ACTION_REACTION(4, R.string.step_action_reaction_title, R.string.step_action_reaction_subtitle, true);

    companion object {
        val totalSteps = entries.size
        fun fromIndex(index: Int): EntryStep = entries[index]
    }
}

data class EntryUiState(
    val situation: String = "",
    val thoughts: String = "",
    val emotions: List<String> = emptyList(),
    val bodyReaction: String = "",
    val actionReaction: String = "",
    val currentStep: EntryStep = EntryStep.SITUATION,
    val direction: Int = 1,
    val isSaving: Boolean = false,
    @StringRes val errorRes: Int? = null
)

sealed class EntryEvent {
    data object SaveSuccess : EntryEvent()
}

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val saveEntryUseCase: SaveEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    private val _events = Channel<EntryEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun updateSituation(text: String) {
        _uiState.update { it.copy(situation = text) }
    }

    fun updateThoughts(text: String) {
        _uiState.update { it.copy(thoughts = text) }
    }

    fun updateBodyReaction(text: String) {
        _uiState.update { it.copy(bodyReaction = text) }
    }

    fun updateActionReaction(text: String) {
        _uiState.update { it.copy(actionReaction = text) }
    }

    fun toggleEmotion(emotion: String) {
        _uiState.update { state ->
            val currentEmotions = state.emotions.toMutableList()
            if (currentEmotions.contains(emotion)) {
                currentEmotions.remove(emotion)
            } else {
                currentEmotions.add(emotion)
            }
            state.copy(emotions = currentEmotions)
        }
    }

    fun goToNextStep() {
        _uiState.update { state ->
            val nextIndex = (state.currentStep.index + 1).coerceAtMost(EntryStep.totalSteps - 1)
            state.copy(
                currentStep = EntryStep.fromIndex(nextIndex),
                direction = 1
            )
        }
    }

    fun goToPreviousStep() {
        _uiState.update { state ->
            val prevIndex = (state.currentStep.index - 1).coerceAtLeast(0)
            state.copy(
                currentStep = EntryStep.fromIndex(prevIndex),
                direction = -1
            )
        }
    }

    fun goToStep(step: EntryStep) {
        _uiState.update { state ->
            state.copy(
                currentStep = step,
                direction = if (step.index > state.currentStep.index) 1 else -1
            )
        }
    }

    val isFirstStep: Boolean
        get() = _uiState.value.currentStep == EntryStep.SITUATION

    val isLastStep: Boolean
        get() = _uiState.value.currentStep == EntryStep.ACTION_REACTION

    fun saveEntry() {
        val state = _uiState.value
        if (state.situation.isBlank()) {
            _uiState.update { it.copy(errorRes = R.string.error_describe_situation) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorRes = null) }
            try {
                val entry = DiaryEntry(
                    situation = state.situation,
                    thoughts = state.thoughts,
                    emotions = state.emotions,
                    bodyReaction = state.bodyReaction,
                    actionReaction = state.actionReaction
                )
                saveEntryUseCase(entry)
                _uiState.update { it.copy(isSaving = false) }
                _events.send(EntryEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorRes = R.string.error_save_entry
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorRes = null) }
    }
}
