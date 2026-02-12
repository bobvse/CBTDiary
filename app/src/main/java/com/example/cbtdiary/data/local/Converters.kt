package com.example.cbtdiary.data.local

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private const val TAG = "Converters"
    private val gson = Gson()
    
    @TypeConverter
    @JvmStatic
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка десериализации списка строк: $value", e)
            emptyList()
        }
    }
    
    @TypeConverter
    @JvmStatic
    fun toStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) {
            return "[]"
        }
        return try {
            gson.toJson(list)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сериализации списка строк: $list", e)
            "[]"
        }
    }
}
