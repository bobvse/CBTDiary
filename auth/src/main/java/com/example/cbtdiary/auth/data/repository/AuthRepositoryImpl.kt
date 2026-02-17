package com.example.cbtdiary.auth.data.repository

import com.example.cbtdiary.auth.data.local.AuthPreferences
import com.example.cbtdiary.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val preferences: AuthPreferences
) : AuthRepository {

    override suspend fun savePin(pin: String) {
        preferences.savePinHash(pin)
    }

    override suspend fun isPinSet(): Boolean {
        return preferences.isPinSet()
    }

    override suspend fun verifyPin(pin: String): Boolean {
        return preferences.verifyPin(pin)
    }
}
