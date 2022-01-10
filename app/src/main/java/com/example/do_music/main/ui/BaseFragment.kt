package com.example.do_music.main.ui

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.do_music.main.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BaseFragment"

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }

    }

}