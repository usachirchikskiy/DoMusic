package com.example.do_music.network.main

import com.example.do_music.model.Instrument
import com.google.gson.annotations.SerializedName

data class GetInstrumentsResponse(
    val total: Int,
    val rows: List<Instrument>
)