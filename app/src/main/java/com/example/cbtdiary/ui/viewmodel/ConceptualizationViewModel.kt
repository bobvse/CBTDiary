package com.example.cbtdiary.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.R
import com.example.cbtdiary.domain.model.Alternative
import com.example.cbtdiary.domain.model.AutomaticThought
import com.example.cbtdiary.domain.model.BackgroundEvent
import com.example.cbtdiary.domain.model.CognitiveDistortion
import com.example.cbtdiary.domain.model.Conceptualization
import com.example.cbtdiary.domain.model.CoreBelief
import com.example.cbtdiary.domain.model.EmotionEntry
import com.example.cbtdiary.domain.model.IntermediateBelief
import com.example.cbtdiary.domain.model.SmerSuggestions
import com.example.cbtdiary.domain.model.Trigger
import com.example.cbtdiary.domain.usecase.concept.DeleteConceptVersionUseCase
import com.example.cbtdiary.domain.usecase.concept.GetAllConceptVersionsUseCase
import com.example.cbtdiary.domain.usecase.concept.GetCurrentConceptualizationUseCase
import com.example.cbtdiary.domain.usecase.concept.ImportSmerDataUseCase
import com.example.cbtdiary.domain.usecase.concept.SaveConceptualizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConceptUiState(
    val conceptualization: Conceptualization? = null,
    val isLoading: Boolean = true,
    @StringRes val errorRes: Int? = null
)

data class EditorState(
    val draft: Conceptualization = Conceptualization(),
    val isSaving: Boolean = false,
    val showVersionNoteDialog: Boolean = false,
    @StringRes val errorRes: Int? = null
)

data class VersionsState(
    val versions: List<Conceptualization> = emptyList(),
    val isLoading: Boolean = true
)

data class SmerImportState(
    val suggestions: SmerSuggestions = SmerSuggestions(),
    val isLoading: Boolean = false,
    val selectedThoughts: Set<String> = emptySet(),
    val selectedEmotions: Set<String> = emptySet(),
    val selectedSituations: Set<String> = emptySet()
)

@HiltViewModel
class ConceptualizationViewModel @Inject constructor(
    private val getCurrentConceptualization: GetCurrentConceptualizationUseCase,
    private val getAllConceptVersions: GetAllConceptVersionsUseCase,
    private val saveConceptualization: SaveConceptualizationUseCase,
    private val deleteConceptVersion: DeleteConceptVersionUseCase,
    private val importSmerData: ImportSmerDataUseCase
) : ViewModel() {

    private val _conceptState = MutableStateFlow(ConceptUiState())
    val conceptState: StateFlow<ConceptUiState> = _conceptState.asStateFlow()

    private val _editorState = MutableStateFlow(EditorState())
    val editorState: StateFlow<EditorState> = _editorState.asStateFlow()

    private val _versionsState = MutableStateFlow(VersionsState())
    val versionsState: StateFlow<VersionsState> = _versionsState.asStateFlow()

    private val _smerImportState = MutableStateFlow(SmerImportState())
    val smerImportState: StateFlow<SmerImportState> = _smerImportState.asStateFlow()

    init {
        loadCurrent()
        loadVersions()
    }

    private fun loadCurrent() {
        viewModelScope.launch {
            getCurrentConceptualization()
                .catch { _conceptState.update { it.copy(isLoading = false, errorRes = R.string.error_load_concept) } }
                .collect { concept ->
                    _conceptState.update { it.copy(conceptualization = concept, isLoading = false, errorRes = null) }
                }
        }
    }

    private fun loadVersions() {
        viewModelScope.launch {
            getAllConceptVersions()
                .catch { _versionsState.update { it.copy(isLoading = false) } }
                .collect { versions ->
                    _versionsState.update { it.copy(versions = versions, isLoading = false) }
                }
        }
    }

    fun startEditing() {
        val current = _conceptState.value.conceptualization ?: Conceptualization()
        _editorState.update { it.copy(draft = current, errorRes = null) }
    }

    fun requestSave() {
        _editorState.update { it.copy(showVersionNoteDialog = true) }
    }

    fun confirmSave(versionNote: String) {
        _editorState.update { it.copy(showVersionNoteDialog = false, isSaving = true) }
        viewModelScope.launch {
            try {
                saveConceptualization(_editorState.value.draft, versionNote)
                _editorState.update { it.copy(isSaving = false) }
            } catch (e: Exception) {
                _editorState.update { it.copy(isSaving = false, errorRes = R.string.error_save_concept) }
            }
        }
    }

    fun cancelSaveDialog() {
        _editorState.update { it.copy(showVersionNoteDialog = false) }
    }

    fun deleteVersion(id: Long) {
        viewModelScope.launch {
            try {
                deleteConceptVersion(id)
            } catch (_: Exception) {
                _conceptState.update { it.copy(errorRes = R.string.error_delete_version) }
            }
        }
    }

    // region Editor field updates

    fun updateBackground(list: List<BackgroundEvent>) {
        updateDraft { copy(background = list) }
    }

    fun updateCoreBeliefs(list: List<CoreBelief>) {
        updateDraft { copy(coreBeliefs = list) }
    }

    fun updateIntermediateBeliefs(list: List<IntermediateBelief>) {
        updateDraft { copy(intermediateBeliefs = list) }
    }

    fun updateCopingStrategies(list: List<String>) {
        updateDraft { copy(copingStrategies = list) }
    }

    fun updateTriggers(list: List<Trigger>) {
        updateDraft { copy(triggers = list) }
    }

    fun updateAutomaticThoughts(list: List<AutomaticThought>) {
        updateDraft { copy(automaticThoughts = list) }
    }

    fun updateEmotions(list: List<EmotionEntry>) {
        updateDraft { copy(emotions = list) }
    }

    fun updateBehavioralPatterns(list: List<String>) {
        updateDraft { copy(behavioralPatterns = list) }
    }

    fun updateAlternatives(list: List<Alternative>) {
        updateDraft { copy(alternatives = list) }
    }

    fun updateStrengths(list: List<String>) {
        updateDraft { copy(strengths = list) }
    }

    fun updateGoals(list: List<String>) {
        updateDraft { copy(goals = list) }
    }

    private inline fun updateDraft(transform: Conceptualization.() -> Conceptualization) {
        _editorState.update { it.copy(draft = it.draft.transform()) }
    }

    // endregion

    // region SMER Import

    fun loadSmerSuggestions() {
        _smerImportState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val suggestions = importSmerData()
                _smerImportState.update {
                    it.copy(
                        suggestions = suggestions,
                        isLoading = false,
                        selectedThoughts = emptySet(),
                        selectedEmotions = emptySet(),
                        selectedSituations = emptySet()
                    )
                }
            } catch (_: Exception) {
                _smerImportState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleThoughtSelection(text: String) {
        _smerImportState.update {
            val newSet = it.selectedThoughts.toMutableSet()
            if (text in newSet) newSet.remove(text) else newSet.add(text)
            it.copy(selectedThoughts = newSet)
        }
    }

    fun toggleEmotionSelection(text: String) {
        _smerImportState.update {
            val newSet = it.selectedEmotions.toMutableSet()
            if (text in newSet) newSet.remove(text) else newSet.add(text)
            it.copy(selectedEmotions = newSet)
        }
    }

    fun toggleSituationSelection(text: String) {
        _smerImportState.update {
            val newSet = it.selectedSituations.toMutableSet()
            if (text in newSet) newSet.remove(text) else newSet.add(text)
            it.copy(selectedSituations = newSet)
        }
    }

    fun applyImport() {
        val import = _smerImportState.value
        val draft = _editorState.value.draft

        val newThoughts = import.selectedThoughts.map { text ->
            AutomaticThought(text = text)
        }
        val newEmotions = import.selectedEmotions.map { text ->
            EmotionEntry(name = text)
        }
        val newTriggers = import.selectedSituations.map { text ->
            Trigger(text = text)
        }

        val existingThoughtTexts = draft.automaticThoughts.map { it.text }.toSet()
        val existingEmotionNames = draft.emotions.map { it.name }.toSet()
        val existingTriggerTexts = draft.triggers.map { it.text }.toSet()

        updateDraft {
            copy(
                automaticThoughts = automaticThoughts + newThoughts.filter { it.text !in existingThoughtTexts },
                emotions = emotions + newEmotions.filter { it.name !in existingEmotionNames },
                triggers = triggers + newTriggers.filter { it.text !in existingTriggerTexts }
            )
        }

        _smerImportState.update {
            it.copy(selectedThoughts = emptySet(), selectedEmotions = emptySet(), selectedSituations = emptySet())
        }
    }

    val totalSelectedImports: Int
        get() = with(_smerImportState.value) {
            selectedThoughts.size + selectedEmotions.size + selectedSituations.size
        }

    // endregion
}
