package com.example.do_music.presentation

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.do_music.presentation.session.SessionManager
import com.example.do_music.util.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "BaseActivity"

@AndroidEntryPoint
abstract class BaseActivity : LocalizationActivity(), UICommunicationListener {

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

}

