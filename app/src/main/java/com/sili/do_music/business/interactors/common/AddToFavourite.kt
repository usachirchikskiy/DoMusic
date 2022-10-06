package com.sili.do_music.business.interactors.common

import android.util.Log
import com.sili.do_music.business.datasources.data.favourites.FavouritesDao
import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.theory.TheoryDao
import com.sili.do_music.business.datasources.data.home.vocal.VocalsDao
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.model.main.Favourite
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.BOOK_ID
import com.sili.do_music.util.Constants.Companion.NOTE_ID
import com.sili.do_music.util.Constants.Companion.VOCALS_ID
import com.sili.do_music.util.Resource
import com.google.gson.JsonObject
import java.net.ConnectException
import kotlin.coroutines.cancellation.CancellationException


private val TAG: String = "AddToFavourite"

class AddToFavourite(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao,
    private val vocalsDao: VocalsDao,
    private val theoryDao: TheoryDao,
    private val favouritesDao: FavouritesDao
) {
    suspend fun execute(
        id: Int = -1,
        isFavourite: Boolean,
        property: String = ""
    ): Resource<String> {
        val bodyRequest = JsonObject()
        try {
            var removeFavouriteId: Int = -1
            var addToFavouriteId: Int
            try {
                when (property) {
                    NOTE_ID -> {
                        if (isFavourite) {
                            bodyRequest.addProperty(property, id)
                            addToFavouriteId = service.addtoFavourite(bodyRequest.toString()).id
                            instrumentsDao.instrumentUpdate(addToFavouriteId, isFavourite, id)
                            val instrument = instrumentsDao.getInstrument(id)
                            favouritesDao.insertFavouriteItem(
                                Favourite(
                                    favoriteId = instrument.favoriteId!!,
                                    logoId = instrument.logoId,
                                    noteId = instrument.noteId,
                                    noteName = instrument.noteName,
                                    filePartId = instrument.partId,
                                    fileClavierId = instrument.clavierId,
                                    compositorId = instrument.compositorId,
                                    compositorName = instrument.compositorName,
                                    instrumentId = instrument.instrumentId,
                                    instrumentName = instrument.instrumentName,
                                    opusEdition = instrument.opusEdition
                                )
                            )

                        } else {
                            removeFavouriteId = instrumentsDao.getFavouriteId(id)
                            service.removeFromFavourites(removeFavouriteId)
                            try {
                                instrumentsDao.updateInstrumentToFalse(
                                    removeFavouriteId,
                                    isFavourite
                                )
                            } catch (e: Exception) {
                                Log.d(TAG, "execute: $e")
                            }
                        }
                    }
                    BOOK_ID -> {
                        if (isFavourite) {
                            bodyRequest.addProperty(property, id)
                            addToFavouriteId = service.addtoFavourite(bodyRequest.toString()).id
                            theoryDao.updateBook(addToFavouriteId, isFavourite, id)
                            val theory = theoryDao.getBook(id)
                            favouritesDao.insertFavouriteItem(
                                Favourite(
                                    compositorId = theory.authorId,
                                    compositorName = theory.authorName,
                                    bookFileId = theory.bookFileId,
                                    bookName = theory.bookName,
                                    bookId = theory.bookId,
                                    bookType = theory.bookType,
                                    logoId = theory.logoId,
                                    opusEdition = theory.opusEdition,
                                    favoriteId = theory.favoriteId!!
                                )
                            )
                        } else {
                            removeFavouriteId = theoryDao.getFavouriteId(id)
                            service.removeFromFavourites(removeFavouriteId)
                            try {
                                theoryDao.updateBookToFalse(removeFavouriteId, isFavourite)
                            } catch (e: Exception) {
                                Log.d(TAG, "execute: $e")
                            }


                        }

                    }
                    VOCALS_ID -> {
                        if (isFavourite) {
                            bodyRequest.addProperty(property, id)
                            addToFavouriteId = service.addtoFavourite(bodyRequest.toString()).id
                            vocalsDao.updateVocal(addToFavouriteId, isFavourite, id)
                            val vocal = vocalsDao.getVocal(id)
                            favouritesDao.insertFavouriteItem(
                                Favourite(
                                    fileClavierId = vocal.clavierId!!,
                                    compositorId = vocal.compositorId!!,
                                    compositorName = vocal.compositorName!!,
                                    logoId = vocal.logoId!!,
                                    noteName = vocal.noteName,
                                    opusEdition = vocal.opusEdition!!,
                                    vocalsId = vocal.vocalsId!!,
                                    favoriteId = vocal.favoriteId!!
                                )
                            )
                        } else {
                            removeFavouriteId = vocalsDao.getFavouriteId(id)
                            service.removeFromFavourites(removeFavouriteId)
                            try {

                            } catch (e: Exception) {
                                vocalsDao.updateVocalToFalse(removeFavouriteId, isFavourite)
                            }
                        }
                    }
                }
                if (!isFavourite) {
                    favouritesDao.deleteFavourite(removeFavouriteId)
                }
                return Resource.success("Updated")
            } catch (e: CancellationException) {
                throw Exception(Constants.ERROR_ADD_TO_FAVOURITES)
            } catch (e: ConnectException) {
                throw Exception(e)
            }
        } catch (throwable: Throwable) {
            return Resource.error<String>(throwable)
        }
    }

}