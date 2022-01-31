package com.example.do_music.ui.main.home.ui.compositors

import com.example.do_music.model.Compositor

data class HomeCompositorsState(
    val isLoading: Boolean = false,
    val compositors: List<Compositor> = listOf(),
    val searchText: String = "",
    val page:Int = 0,
    val error: Throwable?=null,
    val country_filter:String = "",
)