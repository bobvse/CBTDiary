package com.example.cbtdiary.domain.model

import androidx.annotation.StringRes
import com.example.cbtdiary.R

data class Conceptualization(
    val id: Long = 0,
    val version: Int = 1,
    val versionNote: String = "",
    val background: List<BackgroundEvent> = emptyList(),
    val coreBeliefs: List<CoreBelief> = emptyList(),
    val intermediateBeliefs: List<IntermediateBelief> = emptyList(),
    val copingStrategies: List<String> = emptyList(),
    val triggers: List<Trigger> = emptyList(),
    val automaticThoughts: List<AutomaticThought> = emptyList(),
    val emotions: List<EmotionEntry> = emptyList(),
    val behavioralPatterns: List<String> = emptyList(),
    val alternatives: List<Alternative> = emptyList(),
    val strengths: List<String> = emptyList(),
    val goals: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    val isEmpty: Boolean
        get() = background.isEmpty() && coreBeliefs.isEmpty() &&
                intermediateBeliefs.isEmpty() && copingStrategies.isEmpty() &&
                triggers.isEmpty() && automaticThoughts.isEmpty() &&
                emotions.isEmpty() && behavioralPatterns.isEmpty() &&
                alternatives.isEmpty() && strengths.isEmpty() && goals.isEmpty()
}

data class BackgroundEvent(
    val text: String,
    val period: String = ""
)

data class CoreBelief(
    val text: String,
    val strength: Int = 50
)

data class IntermediateBelief(
    val rule: String,
    val assumption: String = "",
    val compensation: String = ""
)

data class Trigger(
    val text: String,
    val smerEntryId: Long? = null
)

data class AutomaticThought(
    val text: String,
    val distortionType: CognitiveDistortion? = null,
    val smerEntryId: Long? = null,
    val frequency: Int = 1
)

data class EmotionEntry(
    val name: String,
    val intensity: Int = 50
)

data class Alternative(
    val oldThought: String = "",
    val newThought: String,
    val believability: Int = 50
)

enum class CognitiveDistortion(
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int
) {
    CATASTROPHIZING(R.string.distortion_catastrophizing, R.string.distortion_catastrophizing_desc),
    BLACK_WHITE(R.string.distortion_black_white, R.string.distortion_black_white_desc),
    MIND_READING(R.string.distortion_mind_reading, R.string.distortion_mind_reading_desc),
    FORTUNE_TELLING(R.string.distortion_fortune_telling, R.string.distortion_fortune_telling_desc),
    EMOTIONAL_REASONING(R.string.distortion_emotional_reasoning, R.string.distortion_emotional_reasoning_desc),
    LABELING(R.string.distortion_labeling, R.string.distortion_labeling_desc),
    PERSONALIZATION(R.string.distortion_personalization, R.string.distortion_personalization_desc),
    OVERGENERALIZATION(R.string.distortion_overgeneralization, R.string.distortion_overgeneralization_desc),
    SHOULD_STATEMENTS(R.string.distortion_should_statements, R.string.distortion_should_statements_desc),
    MAGNIFICATION(R.string.distortion_magnification, R.string.distortion_magnification_desc),
    DISCOUNTING_POSITIVE(R.string.distortion_discounting_positive, R.string.distortion_discounting_positive_desc),
    SELECTIVE_ABSTRACTION(R.string.distortion_selective_abstraction, R.string.distortion_selective_abstraction_desc);
}

data class SmerSuggestions(
    val thoughts: List<SmerSuggestionItem> = emptyList(),
    val emotions: List<SmerSuggestionItem> = emptyList(),
    val situations: List<SmerSuggestionItem> = emptyList()
)

data class SmerSuggestionItem(
    val text: String,
    val frequency: Int,
    val smerEntryIds: List<Long> = emptyList()
)
