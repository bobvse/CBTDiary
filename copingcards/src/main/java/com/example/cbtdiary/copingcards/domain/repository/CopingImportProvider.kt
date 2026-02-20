package com.example.cbtdiary.copingcards.domain.repository

import com.example.cbtdiary.copingcards.domain.model.ImportSuggestion

interface CopingImportProvider {
    suspend fun getSmerSuggestions(): List<ImportSuggestion>
    suspend fun getConceptSuggestions(): List<ImportSuggestion>
}
