package com.example.do_music.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.do_music.BaseActivity
import com.example.do_music.R
import com.example.do_music.databinding.ActivityAuthBinding
import com.example.do_music.databinding.ActivityMainBinding
import com.example.do_music.ui.MainActivity

class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
    }

    private fun setupObservers() {
        sessionManager.state.observe(this, {
            if (it.onStarMainActivity) {
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2000)
            }
        }
        )
    }

    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun downloadFile(fileName: String, uniqueName: String) {

    }

    override fun showBottomNavigation(toShow: Boolean) {

    }

    override fun onAuthActivity() {

    }

}