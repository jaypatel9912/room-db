package com.room.util

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class TimestampConverter {

    companion object {
        private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: String?): Date? {
            return value?.let {
                try {
                    val timeZone = TimeZone.getTimeZone("IST")
                    df.timeZone = timeZone
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
            return value?.let {
                val timeZone = TimeZone.getTimeZone("IST")
                df.timeZone = timeZone
                df.format(it)
            }
        }
    }
}