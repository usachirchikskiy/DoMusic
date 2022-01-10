package com.example.do_music.network.main


import com.example.do_music.model.Vocal

data class GetVocalsResponse(
    val rows: List<Vocal>,
    val total: Int
)