package com.sili.do_music.business.datasources.network.main.home

import com.sili.do_music.business.model.main.Compositor

data class GetCompositorsResponse(
    val total: Int,
    val rows: List<Compositor>

)
