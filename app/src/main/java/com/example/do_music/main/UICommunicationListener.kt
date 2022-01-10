package com.example.do_music.main

import android.content.Context
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.do_music.R

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun downloadFile(fileName: String, uniqueName: String)


    fun hideKeyboadrd()

}

interface StateMessageCallback {
    fun deleteNoteAgreed()
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

private fun Context.displayErrorDialog(
) {
    TODO()
//    MaterialDialog(this)
//        .customView(R.layout.are_u_sure_dialog).show {
//            findViewById<TextView>(R.id.body).text = "message"
//            findViewById<TextView>(R.id.positive_button).setOnClickListener {
//                dismiss()
//            }
//        }
}

private fun Context.deleteNoteDialog(
    compositorName: String,
    stateMessageCallback: StateMessageCallback
) {
    MaterialDialog(this)
        .customView(R.layout.are_u_sure_dialog).show {
            findViewById<TextView>(R.id.body).text = compositorName
            findViewById<TextView>(R.id.positive_button).setOnClickListener {
                stateMessageCallback.deleteNoteAgreed()
                dismiss()
            }
        }
}