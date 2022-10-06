package com.sili.do_music.business.datasources.network.main.home

import com.sili.do_music.business.model.main.Instrument

data class GetInstrumentsResponse(
    val total: Int,
    val rows: List<Instrument>
)