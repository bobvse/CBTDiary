package com.example.cbtdiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.usecase.GetEntryByIdUseCase
import com.example.cbtdiary.domain.usecase.SaveEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val saveEntryUseCase: SaveEntryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()
    
    private var currentEntryId: Long = 0L
    
    fun loadEntry(id: Long) {
        // Если загружаем ту же запись, не делаем ничего
        if (id == currentEntryId && id != 0L) {
            return
        }
        
        currentEntryId = id
        
        if (id == 0L) {
            _uiState.value = EntryUiState()
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val entry = getEntryByIdUseCase(id)
                if (entry != null) {
                    _uiState.value = _uiState.value.copy(
                        entry = entry,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        entry = DiaryEntry(
                            whatHappened = "",
                            feelings = "",
                            whatIWantedToDo = "",
                            whatIDidActually = "",
                            emotions = emptyList()
                        ),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки записи"
                )
            }
        }
    }
    
    fun updateWhatHappened(text: String) {
        _uiState.value = _uiState.value.copy(
            entry = _uiState.value.entry.copy(whatHappened = text)
        )
    }
    
    fun updateFeelings(text: String) {
        _uiState.value = _uiState.value.copy(
            entry = _uiState.value.entry.copy(feelings = text)
        )
    }
    
    fun updateWhatIWantedToDo(text: String) {
        _uiState.value = _uiState.value.copy(
            entry = _uiState.value.entry.copy(whatIWantedToDo = text)
        )
    }
    
    fun updateWhatIDidActually(text: String) {
        _uiState.value = _uiState.value.copy(
            entry = _uiState.value.entry.copy(whatIDidActually = text)
        )
    }
    
    fun toggleEmotion(emotion: String) {
        val currentEmotions = _uiState.value.entry.emotions.toMutableList()
        if (currentEmotions.contains(emotion)) {
            currentEmotions.remove(emotion)
        } else {
            currentEmotions.add(emotion)
        }
        _uiState.value = _uiState.value.copy(
            entry = _uiState.value.entry.copy(emotions = currentEmotions)
        )
    }
    
    fun saveEntry(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                saveEntryUseCase(_uiState.value.entry)
                _uiState.value = _uiState.value.copy(isSaving = false, error = null)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Ошибка сохранения записи"
                )
            }
        }
    }
}
