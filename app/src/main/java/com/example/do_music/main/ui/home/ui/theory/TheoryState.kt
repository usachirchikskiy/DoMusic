package com.example.do_music.main.ui.home.ui.theory

import com.example.do_music.model.TheoryInfo

data class TheoryState(
    val position:Int = -1,
    val isFavourite: Boolean = false,
    val books: List<TheoryInfo> = listOf(),
    val searchText: String = "",
    val page:Int = 0,
    val error:String = "",
    val bookType:String = "",
    )
