package com.example.cbtdiary.auth.domain.usecase

import com.example.cbtdiary.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyPinUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(pin: String): Boolean {
        return repository.verifyPin(pin)
    }
}
