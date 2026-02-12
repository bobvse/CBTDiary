package com.example.cbtdiary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.usecase.DeleteEntryUseCase
import com.example.cbtdiary.domain.usecase.GetAllEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val entries: List<DiaryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadEntries()
    }
    
    private fun loadEntries() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        getAllEntriesUseCase()
            .onEach { entries ->
                _uiState.value = _uiState.value.copy(
                    entries = entries,
                    isLoading = false
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
            .launchIn(viewModelScope)
    }
    
    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            try {
                deleteEntryUseCase(entry)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
