package com.example.cbtdiary.domain.usecase.concept

import com.example.cbtdiary.domain.model.SmerSuggestionItem
import com.example.cbtdiary.domain.model.SmerSuggestions
import com.example.cbtdiary.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ImportSmerDataUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(): SmerSuggestions {
        val entries = diaryRepository.getAllEntries().first()
        if (entries.isEmpty()) return SmerSuggestions()

        val thoughtMap = mutableMapOf<String, MutableList<Long>>()
        val emotionMap = mutableMapOf<String, MutableList<Long>>()
        val situationMap = mutableMapOf<String, MutableList<Long>>()

        entries.forEach { entry ->
            if (entry.thoughts.isNotBlank()) {
                thoughtMap.getOrPut(entry.thoughts.trim()) { mutableListOf() }.add(entry.id)
            }
            entry.emotions.forEach { emotion ->
                emotionMap.getOrPut(emotion.trim()) { mutableListOf() }.add(entry.id)
            }
            if (entry.situation.isNotBlank()) {
                situationMap.getOrPut(entry.situation.trim()) { mutableListOf() }.add(entry.id)
            }
        }

        return SmerSuggestions(
            thoughts = thoughtMap.toSuggestions(),
            emotions = emotionMap.toSuggestions(),
            situations = situationMap.toSuggestions()
        )
    }

    private fun Map<String, List<Long>>.toSuggestions(): List<SmerSuggestionItem> {
        return entries
            .map { (text, ids) -> SmerSuggestionItem(text, ids.size, ids) }
            .sortedByDescending { it.frequency }
    }
}
