package com.room.ui.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.room.AppConstants
import com.room.R
import com.room.model.Note
import com.room.util.AppUtils
import com.room.util.NavigatorUtils

class PwdActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {

    private lateinit var toolbarTitle: TextView
    private lateinit var btnDone: TextView
    private lateinit var btnClose: ImageView
    private lateinit var editPwd: EditText

    private var note: Note? = null
    private var pwdVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pwd)

        initViews()
        setupListeners()
        setupNote()
        AppUtils.openKeyboard(applicationContext)
    }

    private fun initViews() {
        toolbarTitle = findViewById(R.id.title)
        btnClose = findViewById(R.id.btn_close)
        btnDone = findViewById(R.id.btn_done)
        editPwd = findViewById(R.id.edit_pwd)

        toolbarTitle.text = getString(R.string.toolbar_pwd)
    }

    private fun setupListeners() {
        btnClose.setOnClickListener(this)
        btnDone.setOnClickListener(this)
        editPwd.setOnTouchListener(this)
    }

    private fun setupNote() {
        note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(AppConstants.INTENT_TASK, Note::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(AppConstants.INTENT_TASK) as? Note
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
            btnClose -> {
                finish()
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.stay, R.anim.slide_down)
                }
            }

            btnDone -> {
                note?.let { currentNote ->
                    val enteredPassword = editPwd.text.toString()
                    val hashedEnteredPassword = AppUtils.generateHash(enteredPassword)

                    if (currentNote.password == hashedEnteredPassword) {
                        NavigatorUtils.redirectToViewNoteScreen(this, currentNote)
                    } else {
                        AppUtils.showMessage(applicationContext, getString(R.string.error_pwd))
                    }
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
}