package com.sili.do_music.presentation.main

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import com.sili.do_music.R
import com.sili.do_music.databinding.ActivityMainBinding
import com.sili.do_music.presentation.BaseActivity
import com.sili.do_music.presentation.auth.AuthActivity
import com.sili.do_music.presentation.main.account.secondary.changeSuccess.ChangeSuccessFragment
import com.sili.do_music.util.*
import com.sili.do_music.util.Constants.Companion.CHANNEL_ID
import com.sili.do_music.util.Constants.Companion.NO_INTERNET
import com.sili.do_music.util.Constants.Companion.PROGRESS_MAX
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : BaseActivity(), UIMainCommunicationListener,UIMainUpdate,
    NavController.OnDestinationChangedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var constraintLayout: ConstraintLayout? = null
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        constraintLayout = binding.container
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
    }

    private fun setupObservers() {
        viewModel.state.observe(this, Observer {
            it.error?.let { error ->
                when (error.localizedMessage) {
                    Constants.DOWNLOAD_LIMIT -> {
                        showNotificationError()
                        limitExceededDialog(this)
                    }
                    NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    else -> {
                        toast(error.localizedMessage)
                    }
                }
            }
        })

        viewModel.notificationState.observe(this) { notificationState ->
            if (notificationState.begin) {
                showNotificationBegin()
            } else if (notificationState.onComplete) {
                showNotificationDownloaded()
                viewModel.clearValues()
            }

        }


    }

    private fun showNotificationBegin() {
        notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.outline_file_download_20)
                .setContentTitle("DoMusic")
                .setContentText("Download in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(PROGRESS_MAX, 0, true)
        notificationManager.notify(1, notification.build())
    }

    private fun showNotificationDownloaded() {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(
            (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )).absolutePath + File.separator.toString() + viewModel.state.value!!.fileName
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
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

        notification
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setContentIntent(pendingIntent)
            .setContentText(getString(R.string.download_complete))
        notificationManager.notify(1, notification.build())
    }

    private fun showNotificationError() {
        notification.setContentText(getString(R.string.error))
            .setProgress(0, 0, false)
            .setOngoing(false)
        notificationManager.notify(1, notification.build())
    }

    override fun downloadFile(uniqueName: String, fileName: String) {
        if (isOnline(this) == true) {
            viewModel.downloadFile(uniqueName, fileName)
        }
        else{
            operationErrorDialog(this)
        }
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

    override fun enableWaiting() {
        val currentLayout =
            binding.progressBar.layoutParams as ConstraintLayout.LayoutParams // btn is a View here
        currentLayout.topToTop = ConstraintSet.PARENT_ID // resource ID of new parent field
        binding.progressBar.layoutParams = currentLayout

        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun disableWaiting() {
        val currentLayout =
            binding.progressBar.layoutParams as ConstraintLayout.LayoutParams // btn is a View here
        currentLayout.topToTop = ConstraintSet.TOP
        binding.progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        noInternetDialog(this)
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

    override fun isLiked(favId: Int, isFav: Boolean, property: String) {
        viewModel.isLiked(favId,isFav,property)
    }

}

