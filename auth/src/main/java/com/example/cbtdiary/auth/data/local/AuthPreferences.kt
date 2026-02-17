package com.example.cbtdiary.auth.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePinHash(pin: String) {
        prefs.edit()
            .putString(KEY_PIN_HASH, hashPin(pin))
            .apply()
    }

    fun getPinHash(): String? {
        return prefs.getString(KEY_PIN_HASH, null)
    }

    fun isPinSet(): Boolean {
        return getPinHash() != null
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = getPinHash() ?: return false
        return storedHash == hashPin(pin)
    }

    private fun hashPin(pin: String): String {
        val saltedPin = "$SALT$pin"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(saltedPin.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "cbt_diary_auth"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val SALT = "cbt_diary_pin_salt_v1"
    }
}
