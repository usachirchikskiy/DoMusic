package com.example.do_music.network.main.home

import com.example.do_music.model.Compositor

data class GetCompositorsResponse(
    val total: Int,
    val rows: List<Compositor>

)
