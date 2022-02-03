package com.example.do_music.business.datasources.network.main.favourite

import com.example.do_music.business.model.main.Favourite

data class GetFavouritesResponse(
    val rows: List<Favourite>,
    val total: Int
)