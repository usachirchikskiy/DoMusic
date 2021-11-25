package com.example.do_music.network.main

import com.example.do_music.model.Favourite

data class GetFavouritesResponse(
    val rows: List<Favourite>,
    val total: Int
)