package com.sili.do_music.business.datasources.network.main.home

import com.sili.do_music.business.model.main.TheoryInfo

data class GetBooksResponse(
    val total: Int,
    val rows: List<TheoryInfo>
)
