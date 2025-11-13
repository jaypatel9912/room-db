package com.room.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.room.AppConstants
import com.room.R
import com.room.model.Note
import com.room.util.AppUtils

class AddNoteActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
    View.OnClickListener, View.OnTouchListener {

    private lateinit var editTitle: EditText
    private lateinit var editDesc: EditText
    private lateinit var editPwd: EditText
    private lateinit var textTime: TextView
    private lateinit var btnDone: TextView
    private lateinit var toolbarTitle: TextView
    private lateinit var checkBox: AppCompatCheckBox
    private lateinit var btnDelete: ImageView

    private var note: Note? = null
    private var pwdVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        initViews()
        setupListeners()
        setupNote()
        AppUtils.openKeyboard(applicationContext)
    }

    private fun initViews() {
        textTime = findViewById(R.id.text_time)
        toolbarTitle = findViewById(R.id.title)
        editTitle = findViewById(R.id.edit_title)
        editDesc = findViewById(R.id.edit_desc)
        editPwd = findViewById(R.id.edit_pwd)
        checkBox = findViewById(R.id.checkbox)
        btnDelete = findViewById(R.id.btn_close)
        btnDone = findViewById(R.id.btn_done)
    }

    private fun setupListeners() {
        editPwd.setOnTouchListener(this)
        checkBox.setOnCheckedChangeListener(this)
        btnDelete.setOnClickListener(this)
        btnDone.setOnClickListener(this)
    }

    private fun setupNote() {
        note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(AppConstants.INTENT_TASK, Note::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(AppConstants.INTENT_TASK) as? Note
        }

        if (note == null) {
            setupForNewNote()
        } else {
            setupForExistingNote()
        }
    }

    private fun setupForNewNote() {
        toolbarTitle.text = getString(R.string.add_task_title)
        btnDelete.setImageResource(R.drawable.btn_done)
        btnDelete.tag = R.drawable.btn_done
        textTime.text = AppUtils.getFormattedDateString(AppUtils.getCurrentDateTime())
    }

    private fun setupForExistingNote() {
        note?.let { currentNote ->
            toolbarTitle.text = getString(R.string.edit_task_title)
            btnDelete.setImageResource(R.drawable.ic_delete)
            btnDelete.tag = R.drawable.ic_delete

            currentNote.title?.let { title ->
                if (title.isNotEmpty()) {
                    editTitle.setText(title)
                    editTitle.setSelection(editTitle.text.length)
                }
            }

            currentNote.description?.let { description ->
                if (description.isNotEmpty()) {
                    editDesc.setText(description)
                    editDesc.setSelection(editDesc.text.length)
                }
            }

            currentNote.createdAt?.let { createdAt ->
                textTime.text = AppUtils.getFormattedDateString(createdAt)
            }

            currentNote.password?.let { password ->
                if (password.isNotEmpty()) {
                    editPwd.setText(password)
                    editPwd.setSelection(editPwd.text.length)
                }
            }

            checkBox.isChecked = currentNote.encrypt
        }
    }

    private fun togglePwd() {
        if (!pwdVisible) {
            pwdVisible = true
            editPwd.transformationMethod = null
            val drawable =
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_pwd)?.mutate()
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(applicationContext, R.color.line),
                PorterDuff.Mode.MULTIPLY
            )
            editPwd.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            pwdVisible = false
            editPwd.transformationMethod = PasswordTransformationMethod()
            val drawable =
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_pwd)?.mutate()
            drawable?.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
            editPwd.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }
        editPwd.setSelection(editPwd.length())
    }


    override fun onClick(view: View) {
        AppUtils.hideKeyboard(this)

        when (view) {
            btnDelete -> {
                if (btnDelete.tag as Int == R.drawable.btn_done) {
                    setResult(Activity.RESULT_CANCELED)
                } else {
                    val intent = intent
                    intent.putExtra(AppConstants.INTENT_DELETE, true)
                    intent.putExtra(AppConstants.INTENT_TASK, note)
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.stay, R.anim.slide_down)
                }
            }

            btnDone -> {
                val intent = intent
                note?.let { currentNote ->
                    currentNote.title = editTitle.text.toString()
                    currentNote.description = editDesc.text.toString()
                    currentNote.encrypt = checkBox.isChecked
                    currentNote.password = editPwd.text.toString()
                    intent.putExtra(AppConstants.INTENT_TASK, currentNote)
                } ?: run {
                    intent.putExtra(AppConstants.INTENT_TITLE, editTitle.text.toString())
                    intent.putExtra(AppConstants.INTENT_DESC, editDesc.text.toString())
                    intent.putExtra(AppConstants.INTENT_ENCRYPT, checkBox.isChecked)
                    intent.putExtra(AppConstants.INTENT_PWD, editPwd.text.toString())
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.stay, R.anim.slide_down)
                }
            }
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val drawableRight = 2

        if (event.action == MotionEvent.ACTION_UP) {
            if (view.id == R.id.edit_pwd && event.rawX >= (editPwd.right - editPwd.compoundDrawables[drawableRight].bounds.width())) {
                togglePwd()
                return true
            }
        }
        return false
    }

    override fun onCheckedChanged(p0: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            editPwd.visibility = View.VISIBLE
            editPwd.isFocusable = true
        } else {
            editPwd.visibility = View.INVISIBLE
        }
    }
}