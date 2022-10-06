package com.sili.do_music.business.interactors.common

import android.util.Log
import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.theory.TheoryDao
import com.sili.do_music.business.datasources.data.home.vocal.VocalsDao
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.presentation.common.itemSelected.ItemState
import com.sili.do_music.util.Constants.Companion.BOOK_ID
import com.sili.do_music.util.Constants.Companion.VOCALS_ID
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.*

private const val TAG = "SearchItem"

class SearchItem(
    private val openMainApiService: OpenMainApiService,
    private val vocalsDao: VocalsDao,
    private val instrumentsDao: InstrumentsDao,
    private val theoryDao: TheoryDao
) {
    fun execute(
        itemId: Int,
        type: String
    ): Flow<Resource<ItemState>> = flow {
        emit(Resource.loading())
        when (type) {
            BOOK_ID -> {
                try {
                    theoryDao.insertBook(openMainApiService.getBookById(itemId))
                } catch (e: Exception) {
                    Log.d("SearchItem", "execute: $e")
                    emit(Resource.error(e))
                }
                val book = theoryDao.getBook(itemId)
                emit(Resource.success(ItemState(book = book)))
            }
            VOCALS_ID -> {
                try {
                    vocalsDao.insertVocal(openMainApiService.getVocalById(itemId))
                } catch (e: Exception) {
                    Log.d("SearchItem", "execute: $e")
                    emit(Resource.error(e))
                }
                val vocal = vocalsDao.getVocal(itemId)
                emit(Resource.success(ItemState(vocal = vocal)))
            }
            else -> {
                try {
                    instrumentsDao.insertInstrument(openMainApiService.getInstrumentById(itemId))
                } catch (e: Exception) {
                    Log.d("SearchItem", "execute: $e")
                    emit(Resource.error(e))
                }
                val instrument = instrumentsDao.getInstrument(itemId)
                emit(Resource.success(ItemState(instrument = instrument)))
            }
        }
    }
}