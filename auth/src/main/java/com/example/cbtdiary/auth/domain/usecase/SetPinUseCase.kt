package com.example.cbtdiary.auth.domain.usecase

import com.example.cbtdiary.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SetPinUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(pin: String) {
        require(pin.length == 4 && pin.all { it.isDigit() }) {
            "PIN must be exactly 4 digits"
        }
        repository.savePin(pin)
    }
}
