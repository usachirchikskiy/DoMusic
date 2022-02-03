package com.example.do_music.presentation.main

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.do_music.R
import com.example.do_music.databinding.ActivityMainBinding
import com.example.do_music.presentation.BaseActivity
import com.example.do_music.presentation.auth.AuthActivity
import com.example.do_music.presentation.main.account.secondary.changeSuccess.ChangeSuccessFragment
import com.example.do_music.util.Constants
import com.example.do_music.util.StateMessageCallback
import com.example.do_music.util.UIMainCommunicationListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Credentials


private const val TAG = "MainActivity"

class MainActivity : BaseActivity(), UIMainCommunicationListener,
    NavController.OnDestinationChangedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var downloadId: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                Log.d(TAG, "onReceive: " + "downloadCompleted")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
        setSupportActionBar(binding.toolbar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment !is ChangeSuccessFragment) {
                    super.onBackPressed()
                } else {
                    fragment.startAuth()
                }
            }
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
        downloadId = downloadManager!!.enqueue(request)
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


    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun getLocale(): String {
        return getCurrentLanguage().language
    }

    override fun setLocale(language: String) {
        setLanguage(language)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeCompositorSelectedFragment -> {
                binding.appbar.visibility = View.VISIBLE
                val title = arguments?.get(Constants.NAME_OF_COMPOSITOR) as String
                destination.label = title
            }
            R.id.itemSelectedInstrument -> {
                binding.appbar.visibility = View.VISIBLE
            }
            R.id.itemSelectedInstrument2 -> {
                binding.appbar.visibility = View.VISIBLE
            }
            else -> {
                binding.appbar.visibility = View.GONE
            }
        }

        if (destination.id == R.id.changeSuccessFragment
            || destination.id == R.id.changeEmailFragment
            || destination.id == R.id.changePasswordFragment
            || destination.id == R.id.prepareEmailFragment
            || destination.id == R.id.newPasswordFragment
        ) {
            binding.bottomNavigationView.visibility = View.GONE
        } else {
            binding.bottomNavigationView.visibility = View.VISIBLE
        }

    }

}

