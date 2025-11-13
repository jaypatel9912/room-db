package com.room.util

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {

    companion object {
        private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: String?): Date? {
            return value?.let {
                try {
                    df.parse(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        @TypeConverter
        @JvmStatic
        fun dateToTimestamp(value: Date?): String? {
            return value?.let { df.format(it) }
        }
    }
}