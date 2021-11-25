package com.example.do_music

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.example.do_music.auth.AuthActivity
import com.example.do_music.main.MainActivity
import com.example.do_music.session.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    private val TAG = "SplashScreen"
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makefullscreen()
        setContentView(R.layout.activity_splash)
        startactivity()
    }

    private fun startactivity(){
        if (sessionManager.isnull()==false){
            Handler().postDelayed({
                Log.d(TAG, "startactivity: ")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },2000)
        }
        else{
            Handler().postDelayed({
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            },2000)
        }
    }

    private fun makefullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
    }
}