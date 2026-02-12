package com.example.cbtdiary.domain.model

import java.util.Date

data class DiaryEntry(
    val id: Long = 0,
    val whatHappened: String,
    val feelings: String,
    val whatIWantedToDo: String,
    val whatIDidActually: String,
    val emotions: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val formattedDate: String
        get() = Date(createdAt).toString()
}
