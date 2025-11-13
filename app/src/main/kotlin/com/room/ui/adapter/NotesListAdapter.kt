package com.room.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.room.R
import com.room.model.Note
import com.room.util.AppUtils
import com.room.util.NoteDiffUtil

class NotesListAdapter(private var notes: MutableList<Note>) :
    RecyclerView.Adapter<NotesListAdapter.CustomViewHolder>() {

    private val accentColors = arrayOf(
        R.color.accent_purple,
        R.color.accent_blue,
        R.color.accent_green,
        R.color.accent_orange,
        R.color.accent_red
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val note = getItem(position)
        val context = holder.itemView.context

        // Set title
        holder.itemTitle.text = note.title?.takeIf { it.isNotEmpty() } ?: "Untitled Note"

        // Set preview (first few lines of description)
        val preview = note.description?.let { desc ->
            if (desc.length > 120) {
                desc.substring(0, 120) + "..."
            } else {
                desc
            }
        } ?: "No content"
        holder.itemPreview.text = preview

        // Set formatted date
        holder.itemTime.text = note.createdAt?.let { AppUtils.getFormattedDateString(it) } ?: ""

        // Handle lock indicator
        if (note.encrypt) {
            holder.lockIndicator.visibility = View.VISIBLE
            // Apply tint programmatically
            ImageViewCompat.setImageTintList(
                holder.lockIndicator,
                ContextCompat.getColorStateList(context, R.color.edit_title)
            )
        } else {
            holder.lockIndicator.visibility = View.GONE
        }

        // Show category indicator with different colors
        holder.categoryIndicator.visibility = View.VISIBLE

        // Choose color based on note properties
        val colorIndex = when {
            note.encrypt -> 0 // Purple for encrypted notes
            (note.description?.length ?: 0) > 500 -> 1 // Blue for long notes
            note.title?.contains(
                "important",
                ignoreCase = true
            ) == true -> 4 // Red for important notes
            note.title?.contains("todo", ignoreCase = true) == true -> 2 // Green for todos
            else -> (note.id % accentColors.size) // Varied colors based on ID
        }

        val accentColor = ContextCompat.getColor(context, accentColors[colorIndex])
        holder.categoryIndicator.setBackgroundColor(accentColor)
    }

    override fun getItemCount(): Int = notes.size

    fun getItem(position: Int): Note = notes[position]

    fun addTasks(newNotes: List<Note>) {
        val noteDiffUtil = NoteDiffUtil(notes, newNotes)
        val diffResult = DiffUtil.calculateDiff(noteDiffUtil)
        notes.clear()
        notes.addAll(newNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.item_title)
        val itemPreview: TextView = itemView.findViewById(R.id.item_preview)
        val itemTime: TextView = itemView.findViewById(R.id.item_desc)
        val lockIndicator: ImageView = itemView.findViewById(R.id.lock_indicator)
        val categoryIndicator: View = itemView.findViewById(R.id.category_indicator)
    }
}