package com.room.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.room.AppConstants
import com.room.R
import com.room.model.Note
import com.room.repository.NoteRepository
import com.room.ui.adapter.NotesListAdapter
import com.room.util.AppUtils
import com.room.util.NavigatorUtils
import com.room.util.RecyclerItemClickListener

class NotesListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: NotesListAdapter
    private lateinit var noteRepository: NoteRepository
    private val notes = mutableListOf<Note>()

    private val addNoteActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result.resultCode, result.data)
        }

    private val pwdActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        getAllNotes()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.task_list)
        emptyView = findViewById(R.id.empty_view)
        fab = findViewById(R.id.fab)

        noteRepository = NoteRepository(application)
        adapter = NotesListAdapter(notes)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val note = adapter.getItem(position)
                    if (note.encrypt && !note.password.isNullOrEmpty()) {
                        val intent = Intent(
                            this@NotesListActivity,
                            com.room.ui.activity.PwdActivity::class.java
                        )
                        intent.putExtra(AppConstants.INTENT_TASK, note)
                        pwdActivityLauncher.launch(intent)
                    } else {
                        val intent = Intent(
                            this@NotesListActivity,
                            com.room.ui.activity.AddNoteActivity::class.java
                        )
                        intent.putExtra(AppConstants.INTENT_TASK, note)
                        addNoteActivityLauncher.launch(intent)
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        @Suppress("DEPRECATION")
                        overridePendingTransition(R.anim.slide_up, R.anim.stay)
                    }
                }
            })
        )
    }

    private fun setupClickListeners() {
        fab.setOnClickListener {
            val intent =
                Intent(this@NotesListActivity, com.room.ui.activity.AddNoteActivity::class.java)
            addNoteActivityLauncher.launch(intent)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                @Suppress("DEPRECATION")
                overridePendingTransition(R.anim.slide_up, R.anim.stay)
            }
        }
    }

    private fun handleActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                if (intent.getBooleanExtra(AppConstants.INTENT_DELETE, false)) {
                    val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(AppConstants.INTENT_TASK, Note::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getSerializableExtra(AppConstants.INTENT_TASK) as? Note
                    }
                    note?.let {
                        noteRepository.deleteTask(it)
                        AppUtils.showMessage(
                            applicationContext,
                            getString(R.string.task_deleted)
                        )
                    }
                } else {
                    val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(AppConstants.INTENT_TASK, Note::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getSerializableExtra(AppConstants.INTENT_TASK) as? Note
                    }
                    if (note != null) {
                        // Update existing note
                        note.modifiedAt = AppUtils.getCurrentDateTime()
                        noteRepository.updateTask(note)
                        AppUtils.showMessage(
                            applicationContext,
                            getString(R.string.task_updated)
                        )
                    } else {
                        // Create new note
                        val title = intent.getStringExtra(AppConstants.INTENT_TITLE) ?: ""
                        val description = intent.getStringExtra(AppConstants.INTENT_DESC) ?: ""
                        val encrypt = intent.getBooleanExtra(AppConstants.INTENT_ENCRYPT, false)
                        val password = intent.getStringExtra(AppConstants.INTENT_PWD)

                        val newNote = Note().apply {
                            this.title = title
                            this.description = description
                            this.encrypt = encrypt
                            this.password = if (encrypt && !password.isNullOrEmpty()) {
                                AppUtils.generateHash(password)
                            } else null
                            this.createdAt = AppUtils.getCurrentDateTime()
                            this.modifiedAt = AppUtils.getCurrentDateTime()
                        }

                        noteRepository.insertTask(newNote)
                        AppUtils.showMessage(applicationContext, getString(R.string.task_saved))
                    }
                }
            }
        }
    }

    private fun getAllNotes() {
        noteRepository.getTasks().observe(this) { notesList ->
            if (!notesList.isNullOrEmpty()) {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                adapter.addTasks(notesList)
            } else {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
    }
}