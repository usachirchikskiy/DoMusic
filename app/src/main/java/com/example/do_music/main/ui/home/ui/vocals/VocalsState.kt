package com.example.do_music.main.ui.home.ui.vocals

import com.example.do_music.model.Vocal

data class VocalsState(
    val isLoading:Boolean = false,
//    val position: Int = -1,
    val instruments: List<Vocal> = listOf(),
    val searchText: String = " ",
    val pageNumber: Int = 0,
    var error: Throwable? = null,
)
