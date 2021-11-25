package com.example.do_music.network.main

import com.example.do_music.model.Instrument
import com.google.gson.annotations.SerializedName

data class GetInstrumentsResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("rows")
    val rows: List<Instrument>
)