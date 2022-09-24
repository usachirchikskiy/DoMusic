package com.example.do_music.presentation.main.favourites

import com.example.do_music.business.model.main.Favourite
import com.example.do_music.util.Constants.Companion.BOOK

data class FavouriteState(
    val isLoading: Boolean = false,
    val favouriteItems: List<Favourite> = listOf(),
    val searchText: String = "",
    val page:Int = 0,
    val error: Throwable?=null,
    val docType:String = BOOK,
    val isLastPage:Boolean = false
)
