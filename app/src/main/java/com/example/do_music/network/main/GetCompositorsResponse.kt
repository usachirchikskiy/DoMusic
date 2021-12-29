package com.example.do_music.network.main

import com.example.do_music.model.CompositorEntity
import com.google.gson.annotations.SerializedName

data class GetCompositorsResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("rows")
    val rows: List<CompositorEntity>

)
