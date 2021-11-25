package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.instruments.returnOrderedInstrumentsQuery
import com.example.do_music.model.Instrument
import com.example.do_music.network.main.InstrumentByGroup
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "SearchInstruments"

class SearchInstruments(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao
) {

    fun getGroupOfInstruments(instrumentGroupName: String): Flow<Resource<List<InstrumentByGroup>>> =
        flow {

            try {
                val groups = service.getInstrumentsByGroupName(instrumentGroupName)
                emit(Resource.success(data = groups))
            } catch (throwable: Throwable) {
                emit(
                    Resource.error<List<InstrumentByGroup>>(throwable)
                )
            }
        }

    fun execute(
        instrumentId: Int = -1,
        instrumentGroupName: String = "",
        noteGroupType: String = "",
        searchText: String = " ",
        page: Int,
        init: Boolean
    ): Flow<Resource<List<Instrument>>> = flow {
        emit(Resource.loading<List<Instrument>>())
        try {
            lateinit var instruments_response: List<Instrument>
            if (init) {
                instruments_response =
                    service.getListNotes(pageNumber = page, searchText = searchText).rows
            } else if (instrumentId != -1) {
                instruments_response = service.getInstruments(
                    pageNumber = page,
                    instrumentType = instrumentGroupName,
                    noteGroupType = noteGroupType,
                    instrument = instrumentId,
                    searchText = searchText
                ).rows
            } else if (noteGroupType != "") {
                instruments_response = service.getInstruments(
                    pageNumber = page,
                    instrumentType = instrumentGroupName,
                    searchText = searchText,
                    noteGroupType = noteGroupType
                ).rows
            }

            for (instrument in instruments_response) {
                try {
                    instrumentsDao.insertInstrument(instrument)
                } catch (e: Exception) {
                    Log.d(TAG + " Error", e.message.toString())
                    e.printStackTrace()
                }
            }

            val instruments_favourite_response = service.getFavouriteItems(
                pageNumber = 0,
                docType = "NOTES"
            )
            var instruments_favourite_response_rows = instruments_favourite_response.rows
            for (i in instruments_favourite_response_rows) {

                try {
                    i.noteId?.let { instrumentsDao.updateInstrument(it, true) }
                } catch (throwable: Throwable) {
                    val getInstrument = i.noteId?.let { service.getInstrumentById(it) }
                    getInstrument?.let { instrumentsDao.insertInstrument(it) }
                    Log.d(TAG, "execute: " + throwable.message)
                }
            }

            val total_page: Int = instruments_favourite_response.total / 10
            for (i in 1..total_page) {
                instruments_favourite_response_rows = service.getFavouriteItems(
                    pageNumber = i,
                    docType = "NOTES"
                ).rows
                for (j in instruments_favourite_response_rows) {
                    try {
                        j.noteId?.let { instrumentsDao.updateInstrument(it, true) }
                    } catch (throwable: Throwable) {
                        val getInstrument = j.noteId?.let { service.getInstrumentById(it) }
                        getInstrument?.let { instrumentsDao.insertInstrument(it) }
                        Log.d(TAG, "execute: " + throwable.message)
                    }
                }
            }

            Log.d(TAG, "Inet:\n" + instruments_response.toString())
            var cashed_instruments = instrumentsDao.returnOrderedInstrumentsQuery(
                instrumentId = instrumentId,
                instrumentGroupName = instrumentGroupName,
                searchText = searchText,
                page = page + 1,
                noteGroupType = noteGroupType
            )
            Log.d(TAG, "Db:\n" + cashed_instruments.toString())
            emit(Resource.success(data = cashed_instruments))

        } catch (throwable: Throwable) {
            Log.d("Error", throwable.message.toString())
            emit(
                Resource.error<List<Instrument>>(throwable)
            )

        }
    }
}