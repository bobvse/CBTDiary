package com.example.cbtdiary.copingcards.domain.model

import androidx.annotation.StringRes
import com.example.cbtdiary.copingcards.R

data class CopingCard(
    val id: Long = 0,
    val frontText: String = "",
    val backText: String = "",
    val strategies: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val colorIndex: Int = 0,
    val usageCount: Int = 0,
    val lastUsedAt: Long? = null,
    val sourceType: CardSourceType = CardSourceType.MANUAL,
    val sourceId: Long? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

enum class CardSourceType {
    MANUAL,
    SMER_IMPORT,
    CONCEPT_IMPORT
}

enum class PredefinedStrategy(
    @StringRes val labelRes: Int,
    @StringRes val descRes: Int,
    val emoji: String
) {
    BREATHING_478(R.string.strategy_breathing_478, R.string.strategy_breathing_478_desc, "\uD83D\uDCA8"),
    BREATHING_BOX(R.string.strategy_breathing_box, R.string.strategy_breathing_box_desc, "\uD83D\uDFE6"),
    GROUNDING_54321(R.string.strategy_grounding, R.string.strategy_grounding_desc, "\uD83C\uDF3F"),
    FACT_VS_FEAR(R.string.strategy_fact_vs_fear, R.string.strategy_fact_vs_fear_desc, "\u2696\uFE0F"),
    REFRAME(R.string.strategy_reframe, R.string.strategy_reframe_desc, "\uD83D\uDD04"),
    SELF_COMPASSION(R.string.strategy_self_compassion, R.string.strategy_self_compassion_desc, "\u2764\uFE0F"),
    BEHAVIORAL_ACTIVATION(R.string.strategy_behavioral, R.string.strategy_behavioral_desc, "\uD83C\uDFC3"),
    DISTRACTION(R.string.strategy_distraction, R.string.strategy_distraction_desc, "\uD83C\uDFB5");
}

enum class PredefinedTag(
    @StringRes val labelRes: Int,
    val emoji: String
) {
    ANXIETY(R.string.tag_anxiety, "\uD83D\uDE30"),
    DEPRESSION(R.string.tag_depression, "\uD83D\uDE1E"),
    ANGER(R.string.tag_anger, "\uD83D\uDE21"),
    PROCRASTINATION(R.string.tag_procrastination, "\u23F3"),
    SELF_CRITICISM(R.string.tag_self_criticism, "\uD83D\uDCAD"),
    SOCIAL(R.string.tag_social, "\uD83D\uDC65"),
    WORK(R.string.tag_work, "\uD83D\uDCBC"),
    RELATIONSHIPS(R.string.tag_relationships, "\u2764\uFE0F");
}

data class CardColor(
    val frontColor: Long,
    val backColor: Long,
    val name: String
)

val cardColorPalette = listOf(
    CardColor(0xFFE53935, 0xFF43A047, "Red/Green"),
    CardColor(0xFFFF9800, 0xFF00897B, "Orange/Teal"),
    CardColor(0xFF7B1FA2, 0xFF1E88E5, "Purple/Blue"),
    CardColor(0xFFE91E63, 0xFF3F51B5, "Pink/Indigo"),
    CardColor(0xFF795548, 0xFF26A69A, "Brown/Teal"),
    CardColor(0xFFF44336, 0xFF4CAF50, "Crimson/Emerald")
)

data class ImportSuggestion(
    val text: String,
    val secondaryText: String = "",
    val sourceType: CardSourceType,
    val sourceId: Long? = null
)
