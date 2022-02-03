package com.example.do_music.presentation

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.do_music.util.UICommunicationListener
import com.example.do_music.util.UIMainCommunicationListener
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BaseFragment"

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    lateinit var uiCommunicationListener: UICommunicationListener
    lateinit var uiMainCommunicationListener: UIMainCommunicationListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
            uiMainCommunicationListener = context as UIMainCommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }

}