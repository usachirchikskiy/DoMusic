package com.example.do_music.presentation.main.home.ui.theory

import com.example.do_music.business.model.main.TheoryInfo

data class TheoryState(
    val isLoading:Boolean = false,
    val position:Int = -1,
    val books: List<TheoryInfo> = listOf(),
    val searchText: String = "",
    val page:Int = 0,
    var error: Throwable? = null,
    val bookType:String = ""
    )
