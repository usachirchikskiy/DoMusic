package com.example.do_music.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.do_music.BaseActivity
import com.example.do_music.R
import com.example.do_music.ui.auth.AuthActivity

class SplashScreen : BaseActivity() {
    private val TAG = "SplashScreen"

    private var writePermissionGranted = false
    private var readPermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun hideKeyboard() {

    }

    override fun showBottomNavigation(toShow: Boolean) {

    }

    override fun onAuthActivity() {

    }

    override fun displayProgressBar(isLoading: Boolean) {

    }

    override fun downloadFile(fileName: String, uniqueName: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_splash)

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE]
                    ?: readPermissionGranted
                if (writePermissionGranted && readPermissionGranted) {
                    setupObservers()
                }
                else{
                    updateOrRequestPermissions()
                }
            }
        updateOrRequestPermissions()
    }

    private fun setupObservers() {
        sessionManager.state.observe(this, {
            if (it.onStarAuthActivity) {
                Log.d(TAG, "setupObservers: -" + it.onStarAuthActivity)

                Handler().postDelayed({
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }, 2000)

            } else if (it.onStarMainActivity) {

                Log.d(TAG, "setupObservers: +" + it.onStarAuthActivity)
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2000)

            }
        })
    }


    private fun updateOrRequestPermissions() {
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        writePermissionGranted = hasWritePermission || minSdk29

        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        readPermissionGranted = hasReadPermission

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            Log.d(TAG, "updateOrRequestPermissions: " + permissionsToRequest)
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            setupObservers()
        }
    }

    private fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
    }


}