package com.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.room.model.Note

@Dao
interface DaoAccess {

    @Insert
    suspend fun insertTask(note: Note): Long

    @Query("SELECT * FROM Note ORDER BY created_at desc")
    fun fetchAllTasks(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :taskId")
    fun getTask(taskId: Int): LiveData<Note>

    @Update
    suspend fun updateTask(note: Note)

    @Delete
    suspend fun deleteTask(note: Note)
}