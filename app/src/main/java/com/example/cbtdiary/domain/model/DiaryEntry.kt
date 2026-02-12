package com.example.cbtdiary.domain.model

data class DiaryEntry(
    val id: Long = 0,
    val whatHappened: String,
    val feelings: String,
    val whatIWantedToDo: String,
    val whatIDidActually: String,
    val emotions: List<String>,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    companion object {
        const val NEW_ENTRY_ID = 0L
    }
}
