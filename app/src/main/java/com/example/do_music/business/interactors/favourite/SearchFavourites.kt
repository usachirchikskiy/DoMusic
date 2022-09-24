package com.example.do_music.business.interactors.favourite

import android.util.Log
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.model.main.*
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.NOTES
import com.example.do_music.util.Constants.Companion.VOCALS
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow


class SearchFavourites(
    private val service: OpenMainApiService,
    private val favouritesDao: FavouritesDao
) {
    fun execute(
        pageNumber: Int,
        docType: String,
        searchText: String,
    ): Flow<Resource<List<Favourite>>> = flow {
        emit(Resource.loading())

        try {
            val instrumentsFavouriteResponse = service.getFavouriteItems(
                pageNumber = pageNumber,
                docType = docType,
                searchText = searchText
            )
            val favouriteInstruments = instrumentsFavouriteResponse.rows
            if (favouriteInstruments.isNotEmpty()) {
                favouritesDao.insertFavouriteList(favouriteInstruments)
//                when (docType) {
//                    BOOK -> {
//                        favouriteInstruments.forEach {
//                            if (theoryDao.getBook(it.bookId!!) == null) {
//                                val theory = service.getBookById(it.bookId)
//                                theoryDao.insertBook(theory)
//                            }
//                            theoryDao.insertBook(
//                                TheoryInfo(
//                                    authorId = it.compositorId!!,
//                                    authorName = it.compositorName!!,
//                                    bookFileId = it.bookFileId!!,
//                                    bookFileName = it.bookName!!,
//                                    bookId = it.bookId!!,
//                                    bookName = it.bookName,
//                                    bookType = it.bookType!!,
//                                    logoId = it.logoId!!,
//                                    opusEdition = it.opusEdition!!,
//                                    favorite = true,
//                                    favoriteId = it.favoriteId
//                                )
//                            )
//                        }
//                    }
//                    VOCALS -> {
//                        favouriteInstruments.forEach {
//                            if (vocalsDao.getVocal(it.vocalsId!!) == null) {
//                                val vocal = service.getVocalById(it.vocalsId)
//                                vocalsDao.insertVocal(vocal)
//                            }
//                            vocalsDao.insertVocal(
//                                Vocal(
//                                    clavierFileName = it.noteName!!,
//                                    clavierId = it.fileClavierId!!,
//                                    compositorId = it.compositorId!!,
//                                    compositorName = it.compositorName!!,
//                                    logoId = it.logoId!!,
//                                    noteName = it.noteName,
//                                    opusEdition = it.opusEdition!!,
//                                    vocalsId = it.vocalsId!!,
//                                    favorite = true,
//                                    favoriteId = it.favoriteId
//                                )
//                            )
//                        }
//                    }
//                    NOTES -> {
//                        favouriteInstruments.forEach {
//                            if (instrumentsDao.getInstrument(it.noteId!!) == null) {
//                                val instrument = service.getInstrumentById(it.noteId)
//                                instrumentsDao.insertInstrument(instrument)
//                            }
//                        }
//                    }

            } else {
                throw Exception(Constants.LAST_PAGE)
            }

        } catch (throwable: Throwable) {
            emit(
                Resource.error<List<Favourite>>(throwable)
            )
        }

        val cachedBlogs = favouritesDao.getFavItems(
            page = pageNumber + 1,
            searchText = searchText,
            docType = docType
        )

        cachedBlogs.collect {
            emit(Resource.success(data = it))
        }
    }
}
