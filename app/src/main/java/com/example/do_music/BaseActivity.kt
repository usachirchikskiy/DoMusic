package com.example.do_music

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.do_music.main.UICommunicationListener
import com.example.do_music.session.SessionManager
import okhttp3.Credentials
import javax.inject.Inject


private const val TAG = "BaseActivity"

abstract class BaseActivity : AppCompatActivity(), UICommunicationListener {

    @Inject
    lateinit var sessionManager: SessionManager

    private var downloadid: Long = 0

    abstract override fun hideKeyboadrd()

    abstract override fun displayProgressBar(isLoading: Boolean)

    override fun downloadFile(fileName: String, uniqueName: String) {
        Log.d(TAG, "downloadFile: ")

        val new = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadid) Log.d(TAG, "DONE")
            }
        }

        val downloadmanager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri = Uri.parse("https://domusic.uz/api/doc?uniqueName=" + uniqueName)
        val credentials: String =
            Credentials.basic(sessionManager.getlogin(), sessionManager.getpassword())
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.addRequestHeader("Authorization", credentials)
        request.setTitle(fileName)
        request.setDescription("Downloading") //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            fileName
        )
        downloadid = downloadmanager!!.enqueue(request)

        this.registerReceiver(new, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}

