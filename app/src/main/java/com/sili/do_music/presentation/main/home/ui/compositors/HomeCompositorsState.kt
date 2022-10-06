package com.sili.do_music.presentation.main.home.ui.compositors

import com.sili.do_music.business.model.main.Compositor

data class HomeCompositorsState(
    val isLoading: Boolean = false,
    val compositors: List<Compositor> = listOf(),
    val searchText: String = "",
    val page:Int = 0,
    val error: Throwable?=null,
    val country_filter:String = "",
    val isLastPage:Boolean = false
)