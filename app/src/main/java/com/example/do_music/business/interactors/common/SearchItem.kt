package com.example.do_music.business.interactors.common

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.presentation.common.itemSelected.ItemState
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream

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