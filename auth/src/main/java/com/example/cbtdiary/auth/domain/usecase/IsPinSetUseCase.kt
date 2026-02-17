package com.example.cbtdiary.auth.domain.usecase

import com.example.cbtdiary.auth.domain.repository.AuthRepository
import javax.inject.Inject

class IsPinSetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isPinSet()
    }
}
