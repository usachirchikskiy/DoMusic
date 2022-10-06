package com.sili.do_music.business.datasources.network.main.favourite

import com.sili.do_music.business.model.main.Favourite

data class GetFavouritesResponse(
    val rows: List<Favourite>,
    val total: Int
)