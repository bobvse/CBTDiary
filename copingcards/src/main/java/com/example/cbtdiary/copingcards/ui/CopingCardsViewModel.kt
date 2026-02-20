package com.example.cbtdiary.copingcards.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cbtdiary.copingcards.R
import com.example.cbtdiary.copingcards.domain.model.CardSourceType
import com.example.cbtdiary.copingcards.domain.model.CopingCard
import com.example.cbtdiary.copingcards.domain.model.ImportSuggestion
import com.example.cbtdiary.copingcards.domain.repository.CopingCardRepository
import com.example.cbtdiary.copingcards.domain.repository.CopingImportProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeckUiState(
    val cards: List<CopingCard> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val filterTag: String? = null,
    val showFavoritesOnly: Boolean = false,
    val cardCount: Int = 0
) {
    val filteredCards: List<CopingCard>
        get() {
            var result = cards
            if (showFavoritesOnly) result = result.filter { it.isFavorite }
            if (!filterTag.isNullOrEmpty()) result = result.filter { filterTag in it.tags }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                result = result.filter {
                    it.frontText.lowercase().contains(q) ||
                    it.backText.lowercase().contains(q) ||
                    it.tags.any { tag -> tag.lowercase().contains(q) }
                }
            }
            return result
        }
}

data class EditorCardState(
    val card: CopingCard = CopingCard(),
    val isEditing: Boolean = false,
    val currentStep: Int = 0,
    val isSaving: Boolean = false,
    @StringRes val errorRes: Int? = null,
    val importSuggestions: List<ImportSuggestion> = emptyList(),
    val isLoadingImport: Boolean = false,
    val showImportSheet: Boolean = false
)

data class QuizState(
    val cards: List<CopingCard> = emptyList(),
    val currentIndex: Int = 0,
    val isRevealed: Boolean = false,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val isComplete: Boolean = false
) {
    val currentCard: CopingCard? get() = cards.getOrNull(currentIndex)
    val progress: Float get() = if (cards.isEmpty()) 0f else (currentIndex.toFloat() / cards.size)
}

@HiltViewModel
class CopingCardsViewModel @Inject constructor(
    private val repository: CopingCardRepository,
    private val importProvider: CopingImportProvider
) : ViewModel() {

    private val _deckState = MutableStateFlow(DeckUiState())
    val deckState: StateFlow<DeckUiState> = _deckState.asStateFlow()

    private val _editorState = MutableStateFlow(EditorCardState())
    val editorState: StateFlow<EditorCardState> = _editorState.asStateFlow()

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            repository.getAllCards()
                .catch { _deckState.update { it.copy(isLoading = false) } }
                .collect { cards ->
                    _deckState.update { it.copy(cards = cards, isLoading = false, cardCount = cards.size) }
                }
        }
    }

    // region Deck operations

    fun setSearchQuery(query: String) {
        _deckState.update { it.copy(searchQuery = query) }
    }

    fun setFilterTag(tag: String?) {
        _deckState.update { it.copy(filterTag = tag) }
    }

    fun toggleFavoritesOnly() {
        _deckState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    }

    fun toggleFavorite(card: CopingCard) {
        viewModelScope.launch {
            repository.updateCard(card.copy(isFavorite = !card.isFavorite))
        }
    }

    fun deleteCard(id: Long) {
        viewModelScope.launch {
            repository.deleteCard(id)
        }
    }

    fun recordUsage(id: Long) {
        viewModelScope.launch {
            repository.recordUsage(id)
        }
    }

    // endregion

    // region Editor

    fun startNewCard() {
        _editorState.update {
            EditorCardState(card = CopingCard(), isEditing = false, currentStep = 0)
        }
    }

    fun startEditCard(card: CopingCard) {
        _editorState.update {
            EditorCardState(card = card, isEditing = true, currentStep = 0)
        }
    }

    fun updateEditorCard(transform: CopingCard.() -> CopingCard) {
        _editorState.update { it.copy(card = it.card.transform()) }
    }

    fun setEditorStep(step: Int) {
        _editorState.update { it.copy(currentStep = step) }
    }

    fun nextEditorStep() {
        val current = _editorState.value.currentStep
        if (current < 3) {
            _editorState.update { it.copy(currentStep = current + 1, errorRes = null) }
        }
    }

    fun prevEditorStep() {
        val current = _editorState.value.currentStep
        if (current > 0) {
            _editorState.update { it.copy(currentStep = current - 1, errorRes = null) }
        }
    }

    fun saveCard() {
        val card = _editorState.value.card
        if (card.frontText.isBlank()) {
            _editorState.update { it.copy(errorRes = R.string.editor_error_front) }
            return
        }
        if (card.backText.isBlank()) {
            _editorState.update { it.copy(errorRes = R.string.editor_error_back) }
            return
        }

        _editorState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                if (_editorState.value.isEditing) {
                    repository.updateCard(card)
                } else {
                    val count = repository.getCardCount()
                    if (count >= 50) {
                        _editorState.update { it.copy(isSaving = false, errorRes = R.string.editor_error_limit) }
                        return@launch
                    }
                    repository.insertCard(card)
                }
                _editorState.update { it.copy(isSaving = false) }
            } catch (_: Exception) {
                _editorState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun loadSmerImport() {
        _editorState.update { it.copy(isLoadingImport = true, showImportSheet = true) }
        viewModelScope.launch {
            try {
                val suggestions = importProvider.getSmerSuggestions()
                _editorState.update { it.copy(importSuggestions = suggestions, isLoadingImport = false) }
            } catch (_: Exception) {
                _editorState.update { it.copy(isLoadingImport = false) }
            }
        }
    }

    fun loadConceptImport() {
        _editorState.update { it.copy(isLoadingImport = true, showImportSheet = true) }
        viewModelScope.launch {
            try {
                val suggestions = importProvider.getConceptSuggestions()
                _editorState.update { it.copy(importSuggestions = suggestions, isLoadingImport = false) }
            } catch (_: Exception) {
                _editorState.update { it.copy(isLoadingImport = false) }
            }
        }
    }

    fun applyImport(suggestion: ImportSuggestion) {
        _editorState.update {
            val card = it.card
            val updatedCard = when {
                suggestion.secondaryText.isNotBlank() && suggestion.sourceType == CardSourceType.CONCEPT_IMPORT ->
                    card.copy(
                        frontText = suggestion.text,
                        backText = suggestion.secondaryText,
                        sourceType = suggestion.sourceType,
                        sourceId = suggestion.sourceId
                    )
                else ->
                    card.copy(
                        frontText = suggestion.text,
                        sourceType = suggestion.sourceType,
                        sourceId = suggestion.sourceId
                    )
            }
            it.copy(card = updatedCard, showImportSheet = false)
        }
    }

    fun dismissImportSheet() {
        _editorState.update { it.copy(showImportSheet = false) }
    }

    // endregion

    // region Quiz

    fun startQuiz() {
        val cards = _deckState.value.cards.shuffled()
        _quizState.update {
            QuizState(
                cards = cards,
                currentIndex = 0,
                isRevealed = false,
                correctCount = 0,
                totalAnswered = 0,
                isComplete = cards.isEmpty()
            )
        }
    }

    fun revealAnswer() {
        _quizState.update { it.copy(isRevealed = true) }
        _quizState.value.currentCard?.let { card ->
            recordUsage(card.id)
        }
    }

    fun answerQuiz(knewIt: Boolean) {
        val state = _quizState.value
        val newCorrect = state.correctCount + if (knewIt) 1 else 0
        val newTotal = state.totalAnswered + 1
        val nextIndex = state.currentIndex + 1
        val isComplete = nextIndex >= state.cards.size

        _quizState.update {
            it.copy(
                currentIndex = nextIndex,
                isRevealed = false,
                correctCount = newCorrect,
                totalAnswered = newTotal,
                isComplete = isComplete
            )
        }
    }

    // endregion
}
