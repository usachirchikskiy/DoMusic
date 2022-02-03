package com.example.do_music.business.interactors.common

import android.util.Log
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
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


    suspend fun deleteFromFavourite(
        favouriteId: Int
    ) {
        favouritesDao.deleteFavourite(favouriteId)
    }

    fun execute(
        noteId: Int = -1,
        bookId: Int = -1,
        vocalsId: Int = -1,
        favouriteId: Int = -1,
        isFavourite: Boolean,
        property: String = "-1"
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading<String>())
        val body_request = JsonObject()
        try {
            var addToFavouriteId: Int? = null
            var id: Int = -1
            var favorite = true
            if (bookId != -1) {
                id = bookId
            } else if (noteId != -1) {
                id = noteId
            } else if (vocalsId != -1) {
                id = vocalsId
            }
            when {
                isFavourite -> {
                    body_request.addProperty(property, id)
                    addToFavouriteId = service.addtoFavourite(body_request.toString()).id
                }
                else -> {
                    service.removeFromFavourites(favouriteId)
                    deleteFromFavourite(favouriteId)
                    favorite = false
                }
            }
            try {
                when (property) {
                    NOTE_ID -> {
                        if (noteId != -1) {
                            instrumentsDao.instrumentUpdate(addToFavouriteId, favorite, noteId)
                        } else {
                            instrumentsDao.instrumentUpdateToFalse(favorite, favouriteId)
                        }
                    }
                    VOCALS_ID -> {
                        if (vocalsId != -1) {
                            vocalsDao.updateVocal(addToFavouriteId, favorite, vocalsId)
                        } else {
                            vocalsDao.updateVocalToFalse(favouriteId, favorite)
                        }
                    }
                    BOOK_ID -> {
                        if (bookId != -1) {
                            theoryDao.updateBook(addToFavouriteId, favorite, bookId)
                        } else {
                            theoryDao.updateBookToFalse(favouriteId, favorite)
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.d(TAG, "Error : " + e.message.toString())
            }
            emit(Resource.success("Updated"))
        } catch (throwable: Throwable) {
            Log.d(TAG, " Error " + throwable.message.toString())
            emit(
                Resource.error<String>(throwable)
            )
        }

    }
}