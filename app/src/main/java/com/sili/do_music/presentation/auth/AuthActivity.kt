package com.sili.do_music.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View

import com.sili.do_music.databinding.ActivityAuthBinding
import com.sili.do_music.presentation.BaseActivity
import com.sili.do_music.presentation.main.MainActivity

class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
    }

    private fun setupObservers() {
        sessionManager.state.observe(this) {
            if (it.onStarMainActivity) {
                onMainActivity()
            }
        }
    }

    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    fun onMainActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    override fun onAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun showNoInternetDialog() {

    }


}