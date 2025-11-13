package com.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.room.util.TimestampConverter
import java.io.Serializable
import java.util.Date

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var title: String? = null,

    var description: String? = null,

    @ColumnInfo(name = "created_at")
    @field:TypeConverters(TimestampConverter::class)
    var createdAt: Date? = null,

    @ColumnInfo(name = "modified_at")
    @field:TypeConverters(TimestampConverter::class)
    var modifiedAt: Date? = null,

    var encrypt: Boolean = false,

    var password: String? = null
) : Serializable