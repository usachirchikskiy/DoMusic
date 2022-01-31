package com.example.do_music.ui.main.home.ui.compositors.compositorSelected

import com.example.do_music.model.Instrument
import com.example.do_music.model.Vocal

data class CompositorSelectedState(
    val isLoading: Boolean = false,
    val compositorId:Int = -1,
    val searchText: String = "",
    val pageNumber: Int = 0,
    val groupFilters: List<String> = listOf(),
    var filterSelected:String = "",
    val instrumentalCompositions: List<Instrument> = listOf(),
    var error: Throwable?=null,
    val vocalCompositions: List<Vocal> = listOf()
)

