package com.example.do_music.network.main.home

import com.example.do_music.model.TheoryInfo
import com.google.gson.annotations.SerializedName

data class GetBooksResponse(
    val total: Int,
    val rows: List<TheoryInfo>
)
