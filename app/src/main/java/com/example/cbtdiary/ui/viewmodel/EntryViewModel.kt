package com.example.cbtdiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.usecase.GetEntryByIdUseCase
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

data class EntryUiState(
    val entry: DiaryEntry = DiaryEntry(
        whatHappened = "",
        feelings = "",
        whatIWantedToDo = "",
        whatIDidActually = "",
        emotions = emptyList()
    ),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class EntryEvent {
    data object SaveSuccess : EntryEvent()
}

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val saveEntryUseCase: SaveEntryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<EntryEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    private var currentEntryId: Long = DiaryEntry.NEW_ENTRY_ID
    
    fun loadEntry(id: Long) {
        if (id == currentEntryId && id != DiaryEntry.NEW_ENTRY_ID) {
            return
        }
        
        currentEntryId = id
        
        if (id == DiaryEntry.NEW_ENTRY_ID) {
            _uiState.update { EntryUiState() }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val entry = getEntryByIdUseCase(id)
                if (entry != null) {
                    _uiState.update {
                        it.copy(
                            entry = entry,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { EntryUiState() }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки записи"
                    )
                }
            }
        }
    }
    
    fun updateWhatHappened(text: String) {
        _uiState.update { it.copy(entry = it.entry.copy(whatHappened = text)) }
    }
    
    fun updateFeelings(text: String) {
        _uiState.update { it.copy(entry = it.entry.copy(feelings = text)) }
    }
    
    fun updateWhatIWantedToDo(text: String) {
        _uiState.update { it.copy(entry = it.entry.copy(whatIWantedToDo = text)) }
    }
    
    fun updateWhatIDidActually(text: String) {
        _uiState.update { it.copy(entry = it.entry.copy(whatIDidActually = text)) }
    }
    
    fun toggleEmotion(emotion: String) {
        _uiState.update { state ->
            val currentEmotions = state.entry.emotions.toMutableList()
            if (currentEmotions.contains(emotion)) {
                currentEmotions.remove(emotion)
            } else {
                currentEmotions.add(emotion)
            }
            state.copy(entry = state.entry.copy(emotions = currentEmotions))
        }
    }
    
    fun saveEntry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                saveEntryUseCase(_uiState.value.entry)
                _uiState.update { it.copy(isSaving = false, error = null) }
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
