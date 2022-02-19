package com.example.do_music.business.interactors.favourite

import android.util.Log
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.model.main.Favourite
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
        val bodyRequest = JsonObject()
        bodyRequest.addProperty("favoriteId", favoriteId)
        bodyRequest.addProperty("favoriteClass", favoriteClass)
        bodyRequest.addProperty("docType", docType)
        try {
            service.addFavouriteClass(body = bodyRequest.toString())
        }catch (ex:Throwable){
            Log.d(TAG, "addFavClass: " + ex.message.toString())
        }
        when (docType) {
            BOOK -> {
                favouritesDao.addFavBookClassDao(favoriteId = favoriteId, bookClass = favoriteClass)
            }
            VOCALS -> {
                favouritesDao.addFavVocalsClassDao(favoriteId = favoriteId, vocalsClass = favoriteClass)
            }
            else -> {
                favouritesDao.addFavNoteClassDao(favoriteId = favoriteId, notesClass = favoriteClass)
            }
        }
    }

    fun execute(
        pageNumber: Int,
        docType: String,
//        favouriteClass: String,
        searchText: String,
        updated: Boolean
    ): Flow<Resource<List<Favourite>>> = flow {
        emit(Resource.loading<List<Favourite>>())
        Log.d(TAG, "execute: $pageNumber\n$docType")
        try {
            if (!updated) {
                val instrumentsFavouriteResponse = service.getFavouriteItems(
                    pageNumber = pageNumber,
                    docType = docType,
//                    favoriteClass = favouriteClass,
                    searchText = searchText
                )
                val favouriteInstruments = instrumentsFavouriteResponse.rows
                favouritesDao.insertFavourite(favouriteInstruments)
            }
        } catch (throwable: Throwable) {
            Log.d(TAG, "execute: " + throwable.message)
        }
        val cachedBlogs = favouritesDao.getFavItems(
            page = pageNumber + 1,
            searchText = searchText,
            docType = docType,
//            favoriteClass = favouriteClass
        )
        Log.d(TAG, "execute: $cachedBlogs")
        emit(Resource.success(data = cachedBlogs))
    }.catch { e ->
        emit(Resource.error(e))
    }
}