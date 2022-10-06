package com.sili.do_music.presentation.main.home.ui.compositors.compositorSelected

import com.sili.do_music.business.model.main.Instrument
import com.sili.do_music.business.model.main.Vocal

data class CompositorSelectedState(
    val isLoading: Boolean = false,
    val compositorId:Int = -1,
    val searchText: String = "",
    val pageNumber: Int = 0,
    val groupFilters: List<String> = listOf(),
    var filterSelected:String = "",
    val instrumentalCompositions: List<Instrument> = listOf(),
    val error: Throwable?=null,
    val vocalCompositions: List<Vocal> = listOf(),
    val isLastPage:Boolean = false
)

