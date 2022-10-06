package com.sili.do_music.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.sili.do_music.presentation.session.SessionManager
import com.sili.do_music.util.ContextUtils
import com.sili.do_music.util.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


private const val TAG = "BaseActivity"

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity(), UICommunicationListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun hideKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun attachBaseContext(baseContext: Context?) {
        baseContext?.let {
            ContextUtils.onAttach(it)
        }
        super.attachBaseContext(baseContext)
    }

    override fun getLocale(): String {
        return Locale.getDefault().language
    }

    override fun setLocale(language: String) {
        ContextUtils.setLocale(context = this.applicationContext, language)
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        startActivity(intent)
    }

}

