package com.example.do_music.presentation.main.home.ui.instruments

import com.example.do_music.presentation.main.home.adapter.InstrumentHelper
import com.example.do_music.business.model.main.Instrument

data class InstrumentState(
    val isLoading: Boolean = false,
    val instrumentId: Int= -1,
    val instrumentGroupName: String="",
    val noteGroupType: String="",
    val instrumentsGroup: List<InstrumentHelper> = listOf(),
    val instruments: List<Instrument> = listOf(),
    val searchText: String = " ",
    val page: Int = 0,
    var error: Throwable? = null,
    )