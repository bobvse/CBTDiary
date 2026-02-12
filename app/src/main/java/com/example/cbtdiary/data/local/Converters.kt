package com.example.cbtdiary.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()
    
    @TypeConverter
    @JvmStatic
    fun fromStringList(value: String?): List<String> {
        if (value == null || value.isEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    @TypeConverter
    @JvmStatic
    fun toStringList(list: List<String>?): String {
        if (list == null || list.isEmpty()) {
            return "[]"
        }
        return try {
            gson.toJson(list)
        } catch (e: Exception) {
            "[]"
        }
    }
}
