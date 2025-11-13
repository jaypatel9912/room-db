package com.room.util

import androidx.recyclerview.widget.DiffUtil
import com.room.model.Note

class NoteDiffUtil(
    private val oldNoteList: List<Note>,
    private val newNoteList: List<Note>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldNoteList.size

    override fun getNewListSize(): Int = newNoteList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].id == newNoteList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition] == newNoteList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // You can return particular field for changed item
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}