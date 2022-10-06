package com.sili.do_music.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.sili.do_music.R
import com.sili.do_music.databinding.ActivitySplashBinding
import com.sili.do_music.presentation.auth.AuthActivity
import com.sili.do_music.presentation.main.MainActivity
import com.sili.do_music.util.toast

private const val TAG = "SplashScreen"

class SplashScreen : BaseActivity() {

    private var writePermissionGranted = false
    private var readPermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: ActivitySplashBinding

    override fun displayProgressBar(isLoading: Boolean) {

    }

    fun onMainActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }


    override fun onAuthActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }, 2000)
    }

    override fun showNoInternetDialog() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE]
                    ?: readPermissionGranted

                if (writePermissionGranted && readPermissionGranted) {
                    setupObservers()
                } else {
                    val rationalFalgREAD =
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    val rationalFalgWRITE =
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (!rationalFalgREAD && !rationalFalgWRITE) {
                        toast(getString(R.string.permission_handling))
                    } else {
                        updateOrRequestPermissions()
                    }
                }
            }
        updateOrRequestPermissions()
    }

    private fun setupObservers() {
        this.sessionManager.state.observe(this) {
            if (it.onStarAuthActivity) {
                onAuthActivity()
            } else if (it.onStarMainActivity) {
                onMainActivity()
            }
        }
    }


    private fun updateOrRequestPermissions() {
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()
        writePermissionGranted = hasWritePermission || minSdk29
        readPermissionGranted = hasReadPermission

        if (!(writePermissionGranted)) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        permissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }

}