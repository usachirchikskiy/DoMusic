package com.example.do_music.main.ui.home.ui.compositors

import com.example.do_music.model.CompositorInfo

data class HomeCompositorsState(
    val isLoading: Boolean = false,
    val compositors: List<CompositorInfo> = listOf(),
    val searchText: String = "",
    val page:Int = 0,
    val error:String = "",
    val country_filter:String = "",
)