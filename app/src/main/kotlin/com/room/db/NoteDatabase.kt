package com.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.room.dao.DaoAccess
import com.room.model.Note
import com.room.util.TimestampConverter

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun daoAccess(): DaoAccess
}