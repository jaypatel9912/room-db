package com.room.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getFormattedDateString(date: Date): String? {
        return try {
            val spf = SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.getDefault())
            val dateString = spf.format(date)
            val newDate = spf.parse(dateString)
            val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
            newDate?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateHash(password: String): String? {
        return try {
            val md = MessageDigest.getInstance("SHA-512")
            md.update(password.toByteArray())
            val byteData = md.digest()
            Base64.encodeToString(byteData, Base64.NO_WRAP)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun openKeyboard(context: Context) {
        Handler(Looper.getMainLooper()).postDelayed({
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            // Using modern approach or suppressing deprecation for compatibility
            @Suppress("DEPRECATION")
            imm?.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }, 500)
    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}