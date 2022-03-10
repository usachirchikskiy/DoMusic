package com.example.do_music.business.interactors.common

import android.util.Log
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


private val TAG: String = "AddToFavourite"

class AddToFavourite(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao,
    private val vocalsDao: VocalsDao,
    private val theoryDao: TheoryDao,
    private val favouritesDao: FavouritesDao
) {

    private suspend fun deleteFromFavourite(
        favouriteId: Int
    ) {
        favouritesDao.deleteFavourite(favouriteId)
    }

    fun execute(
        id: Int = -1,
        isFavourite: Boolean,
        property: String = ""
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val bodyRequest = JsonObject()
        try {
            var removeFavouriteId: Int = -1
            val addToFavouriteId: Int
            try {
                when (property) {
                    NOTE_ID -> {
                        if (isFavourite) {
                            bodyRequest.addProperty(property, id)
                            addToFavouriteId = service.addtoFavourite(bodyRequest.toString()).id
                            instrumentsDao.instrumentUpdate(addToFavouriteId, isFavourite, id)
                        } else {
                            removeFavouriteId = instrumentsDao.getFavouriteId(id)
                            service.removeFromFavourites(removeFavouriteId)
                            instrumentsDao.updateInstrumentToFalse(removeFavouriteId, isFavourite)
                        }
                    }
                    BOOK_ID -> {
                        if (isFavourite) {
                            bodyRequest.addProperty(property, id)
                            addToFavouriteId = service.addtoFavourite(bodyRequest.toString()).id
                            theoryDao.updateBook(addToFavouriteId, isFavourite, id)
                        } else {
                            removeFavouriteId = theoryDao.getFavouriteId(id)
                            service.removeFromFavourites(removeFavouriteId)
                            theoryDao.updateBookToFalse(removeFavouriteId, isFavourite)
                        }

                    }
                    VOCALS_ID -> {
                        if (isFavourite) {
                            bodyRequest.addProperty(property, id)
                            addToFavouriteId = service.addtoFavourite(bodyRequest.toString()).id
                            vocalsDao.updateVocal(addToFavouriteId, isFavourite, id)
                        } else {
                            removeFavouriteId = vocalsDao.getFavouriteId(id)
                            service.removeFromFavourites(removeFavouriteId)
                            vocalsDao.updateVocalToFalse(removeFavouriteId, isFavourite)
                        }
                    }
                }
                if (!isFavourite){
                    favouritesDao.deleteFavourite(removeFavouriteId)
                }
            } catch (e: Throwable) {
                Log.d(TAG, "execute: $e")
                throw Exception(Constants.ERROR_ADD_TO_FAVOURITES)
            }
            emit(Resource.success("Updated"))
        } catch (throwable: Throwable) {
            emit(
                Resource.error<String>(throwable)
            )
        }

    }
}