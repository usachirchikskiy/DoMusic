package com.example.do_music.ui.main.home.ui.vocals

import com.example.do_music.model.Vocal

data class VocalsState(
    val isLoading:Boolean = false,
    val instruments: List<Vocal> = listOf(),
    val searchText: String = " ",
    val pageNumber: Int = 0,
    var error: Throwable? = null,
)
