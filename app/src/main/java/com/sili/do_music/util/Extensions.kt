package com.sili.do_music.util

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.view.inputmethod.EditorInfo
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.sili.do_music.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

val shimmer = Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
    .setDuration(1000) // how long the shimmering animation takes to do one full sweep
    .setBaseAlpha(0.9f) //the alpha of the underlying children
    .setHighlightAlpha(0.6f)// the shimmer alpha amount
    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
    .setAutoStart(true)
    .build()!!

val shimmerDrawable = ShimmerDrawable().apply {
    setShimmer(shimmer)
}

interface StateMessageCallback {
    fun yes()
}

fun isOnline(context: Context?) = context?.isOnline()

fun operationErrorDialog(context: Context?) {
    context?.displayOperationErrorDialog()
}

fun noInternetDialog(context: Context?) {
    context?.displayErrorDialog()
}

fun accountExistsDialog(context: Context?) {
    context?.displayAccountExistsDialog()
}

fun passwordExistsDialog(context: Context?) {
    context?.displayPasswordExistsDialog()
}

fun limitExceededDialog(context: Context?) {
    context?.displayLimitExceededDialog()
}

fun deleteNote(
    context: Context?,
    compositorName: String,
    stateMessageCallback: StateMessageCallback
) {
    context?.deleteNoteDialog(
        compositorName,
        stateMessageCallback
    )
}

fun logoutDialog(
    context: Context?,
    stateMessageCallback: StateMessageCallback
) {
    context?.displayLogoutDialog(
        stateMessageCallback
    )
}

private fun Context.isOnline(): Boolean {
    return try {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        netInfo != null && netInfo.isConnected
    } catch (e: NullPointerException) {
        e.printStackTrace()
        false
    }
}

private fun Context.displayLogoutDialog(stateMessageCallback: StateMessageCallback) {
    MaterialDialog(this)
        .customView(R.layout.logout_dialog).show {
            findViewById<TextView>(R.id.positive_button).setOnClickListener {
                stateMessageCallback.yes()
                dismiss()
            }
            findViewById<TextView>(R.id.negative_button).setOnClickListener {
                dismiss()
            }
        }
}

private fun Context.displayLimitExceededDialog() {
    MaterialDialog(this)
        .customView(R.layout.limit_exceeded).show{
            cancelOnTouchOutside(true)
        }
}

private fun Context.displayOperationErrorDialog() {
    MaterialDialog(this)
        .customView(R.layout.add_to_fav_error_dialog).show {
            findViewById<TextView>(R.id.ok).setOnClickListener {
                dismiss()
            }
        }
}

private fun Context.displayErrorDialog(
) {
    MaterialDialog(this)
        .customView(R.layout.no_internet_dialog).show {
            findViewById<TextView>(R.id.ok).setOnClickListener {
                dismiss()
            }
        }
}

private fun Context.displayAccountExistsDialog() {
    MaterialDialog(this)
        .customView(R.layout.account_exists).show {
            findViewById<TextView>(R.id.ok).setOnClickListener {
                dismiss()
            }
        }
}

private fun Context.displayPasswordExistsDialog() {
    MaterialDialog(this)
        .customView(R.layout.password_exists).show {
            findViewById<TextView>(R.id.ok).setOnClickListener {
                dismiss()
            }
        }
}

private fun Context.deleteNoteDialog(
    compositorName: String,
    stateMessageCallback: StateMessageCallback
) {
    MaterialDialog(this)
        .customView(R.layout.are_u_sure_dialog).show {
            findViewById<TextView>(R.id.body).text = compositorName
            findViewById<TextView>(R.id.positive_button).setOnClickListener {
                stateMessageCallback.yes()
                dismiss()
            }
            findViewById<TextView>(R.id.negative_button).setOnClickListener {
                dismiss()
            }
        }
}

fun EditText.hide(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            clearFocus()
            return@setOnEditorActionListener true
        }
        false
    }
}

fun Context.toast(message:String){
    Toast.makeText(this, message , Toast.LENGTH_LONG).show()
}

fun getMimeType(fileExtension:String):String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
        .toString()
}

fun setGradient(textView: TextView) {
    val paint = textView.paint
    val width = paint.measureText(textView.text.toString())
    val textShader: Shader = LinearGradient(
        0f, 0f, width, textView.textSize, intArrayOf(
            Color.parseColor("#8E2DE2"),
            Color.parseColor("#4A00E0")
        ), null, Shader.TileMode.REPEAT
    )
    textView.paint.shader = textShader
}