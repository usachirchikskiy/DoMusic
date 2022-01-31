package com.example.do_music.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.do_music.BaseActivity
import com.example.do_music.R
import com.example.do_music.databinding.ActivityMainBinding
import com.example.do_music.ui.auth.AuthActivity
import com.example.do_music.ui.common.changeSuccess.ChangeSuccessFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.do_music.ui.main.account.AccountFragment
import com.example.do_music.util.Constants
import okhttp3.Credentials


private const val TAG = "MainActivity"

class MainActivity : BaseActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var downloadid: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    override fun downloadFile(fileName: String, uniqueName: String) {
        val downloadManager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri = Uri.parse(Constants.BASE_URL + Constants.DOWNLOAD_FILE_PART_LINK + uniqueName)
        val credentials: String =
            Credentials.basic(
                sessionManager.state.value?.login,
                sessionManager.state.value?.password
            )
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.addRequestHeader(Constants.AUTHORIZATION, credentials)
        request.setTitle(fileName)
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            fileName
        )
        downloadid = downloadManager!!.enqueue(request)
    }

    override fun showBottomNavigation(toShow: Boolean) {
        if(toShow){
            bottomNavigationView.visibility = View.VISIBLE
        }
        else{
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun onAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setupWithNavController(navController)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onBackPressed() {

        val fragment =
            this.supportFragmentManager.findFragmentById(com.example.do_music.R.id.newsNavHostFragment)

        if (fragment !is ChangeSuccessFragment) {
            super.onBackPressed()
        }
        else{
            Log.d(TAG, "onBackPressed: ")
            onAuthActivity()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}