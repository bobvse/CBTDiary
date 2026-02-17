package com.example.cbtdiary.auth.domain.repository

interface AuthRepository {
    suspend fun savePin(pin: String)
    suspend fun isPinSet(): Boolean
    suspend fun verifyPin(pin: String): Boolean
}
