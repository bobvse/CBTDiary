package com.example.cbtdiary.data.local

import android.util.Log
import androidx.room.TypeConverter
import com.example.cbtdiary.domain.model.Alternative
import com.example.cbtdiary.domain.model.AutomaticThought
import com.example.cbtdiary.domain.model.BackgroundEvent
import com.example.cbtdiary.domain.model.CoreBelief
import com.example.cbtdiary.domain.model.EmotionEntry
import com.example.cbtdiary.domain.model.IntermediateBelief
import com.example.cbtdiary.domain.model.Trigger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private const val TAG = "Converters"
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(list: List<String>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromBackgroundEventList(value: String?): List<BackgroundEvent> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toBackgroundEventList(list: List<BackgroundEvent>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromCoreBeliefList(value: String?): List<CoreBelief> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toCoreBeliefList(list: List<CoreBelief>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromIntermediateBeliefList(value: String?): List<IntermediateBelief> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toIntermediateBeliefList(list: List<IntermediateBelief>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromTriggerList(value: String?): List<Trigger> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toTriggerList(list: List<Trigger>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromAutomaticThoughtList(value: String?): List<AutomaticThought> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toAutomaticThoughtList(list: List<AutomaticThought>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromEmotionEntryList(value: String?): List<EmotionEntry> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toEmotionEntryList(list: List<EmotionEntry>?): String = serialize(list)

    @TypeConverter
    @JvmStatic
    fun fromAlternativeList(value: String?): List<Alternative> {
        if (value.isNullOrEmpty()) return emptyList()
        return deserialize(value)
    }

    @TypeConverter
    @JvmStatic
    fun toAlternativeList(list: List<Alternative>?): String = serialize(list)

    private inline fun <reified T> deserialize(value: String): T {
        return try {
            val type = object : TypeToken<T>() {}.type
            gson.fromJson(value, type) ?: defaultFor()
        } catch (e: Exception) {
            Log.e(TAG, "Deserialization error: $value", e)
            defaultFor()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> defaultFor(): T {
        return emptyList<Any>() as T
    }

    private fun <T> serialize(data: T?): String {
        if (data == null || (data is List<*> && data.isEmpty())) return "[]"
        return try {
            gson.toJson(data)
        } catch (e: Exception) {
            Log.e(TAG, "Serialization error: $data", e)
            "[]"
        }
    }
}
