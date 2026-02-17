package com.example.cbtdiary.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.DiaryEntry
import com.example.cbtdiary.domain.usecase.DeleteEntryUseCase
import com.example.cbtdiary.domain.usecase.GetEntryByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ViewEntryUiState(
    val entry: DiaryEntry? = null,
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null
)

@HiltViewModel
class ViewEntryViewModel @Inject constructor(
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {

    sealed class ViewEvent {
        data object DeleteSuccess : ViewEvent()
    }

    private val _uiState = MutableStateFlow(ViewEntryUiState())
    val uiState: StateFlow<ViewEntryUiState> = _uiState.asStateFlow()

    private val _events = Channel<ViewEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loadedEntryId: Long = -1

    fun loadEntry(id: Long) {
        if (id == loadedEntryId) return
        loadedEntryId = id

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            try {
                val entry = getEntryByIdUseCase(id)
                _uiState.update {
                    it.copy(
                        entry = entry,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorRes = R.string.error_load_entry
                    )
                }
            }
        }
    }

    fun deleteEntry() {
        val entry = _uiState.value.entry ?: return
        viewModelScope.launch {
            try {
                deleteEntryUseCase(entry)
                _events.send(ViewEvent.DeleteSuccess)
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorRes = R.string.error_delete_entry)
                }
            }
        }
    }
}
