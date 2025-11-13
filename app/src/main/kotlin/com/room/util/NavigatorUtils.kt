package com.room.util

import android.app.Activity
import android.content.Intent
import com.room.AppConstants
import com.room.model.Note
import com.room.ui.activity.AddNoteActivity
import com.room.ui.activity.PwdActivity

object NavigatorUtils {

    @Deprecated("Use activity result launcher instead")
    fun redirectToPwdScreen(activity: Activity, note: Note) {
        val intent = Intent(activity, PwdActivity::class.java)
        intent.putExtra(AppConstants.INTENT_TASK, note)
        @Suppress("DEPRECATION")
        activity.startActivityForResult(intent, AppConstants.ACTIVITY_REQUEST_CODE)
    }

    @Deprecated("Use activity result launcher instead")
    fun redirectToEditTaskScreen(activity: Activity, note: Note) {
        val intent = Intent(activity, AddNoteActivity::class.java)
        intent.putExtra(AppConstants.INTENT_TASK, note)
        @Suppress("DEPRECATION")
        activity.startActivityForResult(intent, AppConstants.ACTIVITY_REQUEST_CODE)
    }

    fun redirectToViewNoteScreen(activity: Activity, note: Note) {
        val intent = Intent(activity, AddNoteActivity::class.java)
        intent.putExtra(AppConstants.INTENT_TASK, note)
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        activity.startActivity(intent)
        activity.finish()
    }
}