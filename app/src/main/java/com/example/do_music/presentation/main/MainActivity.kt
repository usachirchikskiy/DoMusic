package com.example.do_music.presentation.main

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
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
import com.example.do_music.util.*
import com.example.do_music.util.Constants.Companion.CHANNEL_ID
import com.example.do_music.util.Constants.Companion.PROGRESS_MAX
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File


private const val TAG = "MainActivity"

class MainActivity : BaseActivity(), UIMainCommunicationListener,
    NavController.OnDestinationChangedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainActivityViewModel by viewModels()
    private var noInternet = false

    //    private var downloadId: Long = 0
//    private lateinit var downloadManager: DownloadManager
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat

//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//            if (id == downloadId) {
//                Log.d(TAG, "onReceive: $id")
//            }

//            val action = intent!!.action
//            Log.d(TAG, "onReceive: ${action.toString()}")
//            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
//                val imageDownloadQuery = DownloadManager.Query()
//                //set the query filter to our previously Enqueued download
//                imageDownloadQuery.setFilterById(downloadId)
//
//                //Query the download manager about downloads that have been requested.
//                val cursor = downloadManager.query(imageDownloadQuery)
//                if (cursor.moveToFirst()) {
//                    Toast.makeText(this@MainActivity, downloadStatus(cursor), Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        }
//    }

//    private fun downloadStatus(cursor: Cursor): String {
//
//        //column for download  status
//        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//        val status = cursor.getInt(columnIndex);
//        //column for reason code if the download failed or paused
//        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
//        val reason = cursor.getInt(columnReason);
//
//        Log.d(
//            TAG, "downloadStatus: $status" + "\n$reason"
//                    + "\n${DownloadManager.STATUS_FAILED}" + "\n${DownloadManager.STATUS_SUCCESSFUL}"
//        )
//        return status.toString()
//    }

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
        notificationManager = NotificationManagerCompat.from(this)

//        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        setupObservers()

    }

    private fun setupObservers() {
        viewModel.state.observe(this, Observer {

            when {
                it.progress != 0 && it.progress % 10 == 0 && it.progress!=100 -> {
                    notification.setContentText(it.progress.toString() + "%")
                        .setProgress(PROGRESS_MAX, it.progress, false)
                    notificationManager.notify(1, notification.build())
                }
                it.progress == 100 -> {
                    Log.d(TAG, "setupObservers: showNotificationDownload")
                    showNotificationDownloaded()
                    viewModel.clearValues()
                }
            }

            it.error?.let {
                notification.setContentText("Failed")
                notificationManager.notify(1, notification.build())
                limitExceededDialog(this)
            }
        })

        viewModel.beginDownload.observe(this, Observer { beginDownload ->
            if (beginDownload) {
                notification =
                    NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.outline_file_download_20)
                        .setContentTitle("DoMusic")
                        .setContentText("Download in progress")
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .setProgress(PROGRESS_MAX, 0, true)

                //Initial Alert
                notificationManager.notify(1, notification.build())
                viewModel.beginDownload.value = false
            }
        })
    }

    private fun showNotificationDownloaded() {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(
            (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )).absolutePath + File.separator.toString() + viewModel.state.value!!.nameOfFile
        )
        val mimeType = getMimeType(file.extension)

        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(
                FileProvider.getUriForFile(
                    applicationContext,
                    this.applicationContext.packageName.toString() + ".provider",
                    file
                ), mimeType
            )
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimeType)
        }
        Log.d(TAG, "showNotificationDownloaded: ${file.path}")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        Log.d(TAG, "setupObservers: ${file.name},${file.path}")
        notification.setContentText("Download complete")
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setContentIntent(pendingIntent)

        notificationManager.notify(1, notification.build())
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(receiver)
//    }

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

    //
    override fun downloadFile(uniqueName: String, fileName: String) {
        viewModel.downloadFile(uniqueName, fileName, this)
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(
//            this, 0, intent, 0
//        )

        //Sets the maximum progress as 100

        //Creating a notification and setting its various attributes

    }

//        Log.d(TAG, "downloadFile: $fileName\t$uniqueName")
//        downloadManager = (this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?)!!
//        val uri = Uri.parse(Constants.BASE_URL + Constants.DOWNLOAD_FILE_PART_LINK + uniqueName)
//        val credentials: String =
//            Credentials.basic(
//                sessionManager.state.value?.login,
//                sessionManager.state.value?.password
//            )
//        val request = DownloadManager.Request(uri)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.addRequestHeader(Constants.AUTHORIZATION, credentials)
//        request.setTitle(fileName)
//        request.setDescription("Downloading")
//
//        request.setDestinationInExternalPublicDir(
//            Environment.DIRECTORY_DOWNLOADS,
//            fileName
//        )
//        downloadManager.enqueue(request)
//    }

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
            cursor.close()
            stateMessageCallback.uploadPhoto(picturePath)
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

    override fun showNoInternetDialog() {
        if (!noInternet) {
            noInternetDialog(this)
        }
        noInternet = true
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

