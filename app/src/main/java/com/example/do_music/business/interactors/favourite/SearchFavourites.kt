package com.example.do_music.business.interactors.favourite

import android.util.Log
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.model.main.Favourite
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.VOCALS
import com.example.do_music.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

private const val TAG = "SearchFavourites"
class SearchFavourites(
    private val service: OpenMainApiService,
    private val favouritesDao: FavouritesDao
) {

    suspend fun addFavClass(
        favoriteId: Int,
        favoriteClass: String,
        docType: String
    ) {
        val body_request = JsonObject()
        body_request.addProperty("favoriteId", favoriteId)
        body_request.addProperty("favoriteClass", favoriteClass)
        body_request.addProperty("docType", docType)
        try {
            service.addFavouriteClass(body = body_request.toString())
        }catch (ex:Throwable){
            Log.d(TAG, "addFavClass: " + ex.message.toString())
        }
        if(docType == BOOK){
            favouritesDao.addFavBookClassDao(favoriteId = favoriteId, bookClass = favoriteClass)
        }
        else if(docType==VOCALS){
            favouritesDao.addFavVocalsClassDao(favoriteId = favoriteId, vocalsClass = favoriteClass)
        }
        else{
            favouritesDao.addFavNoteClassDao(favoriteId = favoriteId, notesClass = favoriteClass)
        }
    }

    fun execute(
        pageNumber: Int,
        docType: String,
        favouriteClass: String,
        searchText: String,
        updated: Boolean
    ): Flow<Resource<List<Favourite>>> = flow {
        emit(Resource.loading<List<Favourite>>())
        try {
            if (!updated) {
                val instruments_favourite_response = service.getFavouriteItems(
                    pageNumber = pageNumber,
                    docType = docType,
                    favoriteClass = favouriteClass,
                    searchText = searchText
                )
                var favourite_instruments = instruments_favourite_response.rows
                favouritesDao.insertFavourite(favourite_instruments)
            }
        } catch (throwable: Throwable) {
            Log.d(TAG, "execute: " + throwable.message)
        }
        val cachedBlogs = favouritesDao.getFavItems(
            page = pageNumber + 1,
            searchText = searchText,
            docType = docType,
            favoriteClass = favouriteClass
        )
        emit(Resource.success(data = cachedBlogs))
    }.catch { e ->
        emit(Resource.error(e))
    }
}