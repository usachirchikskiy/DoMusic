package com.example.do_music.main.ui.account

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchFavourites
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "AccountViewModel"

@HiltViewModel
class AccountViewModel
@Inject
constructor(
    private val searchFavourites: SearchFavourites,
    private val update: AddToFavourite
) : ViewModel() {
    init {
        Log.d(TAG, ": INIT")
    }
}