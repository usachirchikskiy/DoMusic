package com.example.do_music.network.main

import com.example.do_music.model.TheoryInfo
import com.google.gson.annotations.SerializedName

data class GetBooksResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("rows")
    val rows: List<TheoryInfo>
)
