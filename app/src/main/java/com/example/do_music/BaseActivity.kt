package com.example.do_music

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.do_music.session.SessionManager
import com.example.do_music.ui.StateMessageCallback
import com.example.do_music.ui.UICommunicationListener
import com.example.do_music.util.Constants.Companion.AUTHORIZATION
import com.example.do_music.util.Constants.Companion.BASE_URL
import com.example.do_music.util.Constants.Companion.DOWNLOAD_FILE_PART_LINK
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Credentials
import java.util.*
import javax.inject.Inject


private const val TAG = "BaseActivity"

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity(), UICommunicationListener {

    @Inject
    lateinit var sessionManager: SessionManager

    fun setLocale(language:String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
    }

    override fun hideKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun uploadPhotoToServer(uri: Uri, stateMessageCallback: StateMessageCallback) {
//         MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(
            uri,
            filePathColumn, null, null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            stateMessageCallback.uploadPhoto(picturePath)
            cursor.close()
        }
    }

}

