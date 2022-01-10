package com.example.do_music.main.ui.home.ui.instruments

import com.example.do_music.model.Instrument

data class NotesByCompositorState(
    val isLoading: Boolean = false,
    val compositorId:Int = -1,
    val noteGroupType: String = "",
    val searchText: String = "",
    val pageNumber: Int = 0,
    var error: Throwable?=null,
    val instruments: List<Instrument> = listOf()
)

