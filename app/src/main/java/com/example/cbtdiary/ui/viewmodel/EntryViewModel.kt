package com.example.cbtdiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

enum class EntryStep(val index: Int, val title: String, val subtitle: String) {
    SITUATION(0, "Ситуация", "Что произошло?"),
    THOUGHTS(1, "Мысли", "О чём подумал(а) в этот момент?"),
    EMOTION(2, "Эмоция", "Что почувствовал(а)?"),
    BODY_REACTION(3, "Телесная реакция", "Что почувствовал(а) в теле?"),
    ACTION_REACTION(4, "Реакция действия", "Что сделал(а)?");

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
    val error: String? = null
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
            _uiState.update { it.copy(error = "Опишите ситуацию") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
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
                        error = e.message ?: "Ошибка сохранения записи"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
