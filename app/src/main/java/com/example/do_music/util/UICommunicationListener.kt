package com.example.do_music.util

import android.content.Context
import android.net.Uri
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.do_music.R

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun hideKeyboard()

}
interface UIMainCommunicationListener {

    fun downloadFile(fileName: String, uniqueName: String)

    fun uploadPhotoToServer(
        uri: Uri,
        stateMessageCallback: StateMessageCallback
    )

    fun setLocale(language:String)

    fun getLocale():String

    fun onAuthActivity()

}


interface StateMessageCallback {
    fun yes()

    fun uploadPhoto(selectedImagePath: String)
}

fun noInternet(context: Context?) {
    context?.displayErrorDialog()
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
)
{
    context?.displayLogoutDialog(
        stateMessageCallback
    )
}

private fun Context.displayLogoutDialog(stateMessageCallback: StateMessageCallback){
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

private fun Context.displayErrorDialog(
) {
    MaterialDialog(this)
        .customView(R.layout.no_internet_dialog).show {
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
