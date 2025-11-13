package com.room.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.room.db.NoteDatabase
import com.room.model.Note
import com.room.util.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteRepository(context: Context) {

    private val dbName = "db_task"
    private val noteDatabase: NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, dbName).build()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun insertTask(title: String, description: String) {
        insertTask(title, description, false, null)
    }

    fun insertTask(title: String, description: String, encrypt: Boolean, password: String?) {
        val note = Note().apply {
            this.title = title
            this.description = description
            this.createdAt = AppUtils.getCurrentDateTime()
            this.modifiedAt = AppUtils.getCurrentDateTime()
            this.encrypt = encrypt
            this.password = if (encrypt) AppUtils.generateHash(password ?: "") else null
        }
        insertTask(note)
    }

    fun insertTask(note: Note) {
        coroutineScope.launch {
            noteDatabase.daoAccess().insertTask(note)
        }
    }

    fun updateTask(note: Note) {
        note.modifiedAt = AppUtils.getCurrentDateTime()
        coroutineScope.launch {
            noteDatabase.daoAccess().updateTask(note)
        }
    }

    // Removed deleteTask by id using observeForever - use deleteTask(note: Note) instead.

    fun deleteTask(note: Note) {
        coroutineScope.launch {
            noteDatabase.daoAccess().deleteTask(note)
        }
    }

    fun getTask(id: Int): LiveData<Note> {
        return noteDatabase.daoAccess().getTask(id)
    }

    fun getTasks(): LiveData<List<Note>> {
        return noteDatabase.daoAccess().fetchAllTasks()
    }
}