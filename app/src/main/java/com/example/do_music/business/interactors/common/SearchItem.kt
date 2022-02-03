package com.example.do_music.business.interactors.common

import android.util.Log
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.presentation.common.itemSelected.ItemState
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchItem(
    private val vocalsDao: VocalsDao,
    private val instrumentsDao: InstrumentsDao,
    private val theoryDao: TheoryDao
) {
    fun execute(
        itemId: Int,
        type: String
    ): Flow<Resource<ItemState>> = flow {
        emit(Resource.loading())
        try {
            when (type) {
                BOOK_ID -> {
                    val book = theoryDao.getBook(itemId)
                    emit(Resource.success(ItemState(book = book)))
                }
                VOCALS_ID -> {
                    val vocal = vocalsDao.getVocal(itemId)
                    emit(Resource.success(ItemState(vocal = vocal)))
                }
                else -> {
                    val instrument = instrumentsDao.getInstrument(itemId)
                    emit(Resource.success(ItemState(instrument = instrument)))
                }
            }
        }
        catch (throwable: Throwable) {
            Log.d("Error", throwable.message.toString())
            emit(
                Resource.error<ItemState>(throwable)
            )
        }
    }
}