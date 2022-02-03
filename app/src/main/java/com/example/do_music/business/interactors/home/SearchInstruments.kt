package com.example.do_music.business.interactors.home

import android.util.Log
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.instruments.returnOrderedInstrumentsQuery
import com.example.do_music.business.model.main.Instrument
import com.example.do_music.business.datasources.network.main.home.InstrumentByGroup
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

private const val TAG = "SearchInstruments"

class SearchInstruments(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao
) {
    suspend fun serviceAndDb(notes_response: List<Instrument>) = withContext(Dispatchers.IO) {
        for (note in notes_response) {
            try {
                instrumentsDao.insertInstrument(note)
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                e.printStackTrace()
            }
        }
    }

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
        update: Boolean
    ): Flow<Resource<List<Instrument>>> = flow {
        emit(Resource.loading<List<Instrument>>())

        lateinit var instruments_response: List<Instrument>
        if (!update) {
            try {
                if (instrumentId != -1) {
                    instruments_response = service.getInstruments(
                        pageNumber = page,
                        instrumentType = instrumentGroupName,
                        noteGroupType = noteGroupType,
                        instrument = instrumentId,
                        searchText = searchText
                    ).rows
                } else {
                    instruments_response = service.getInstruments(
                        pageNumber = page,
                        instrumentType = instrumentGroupName,
                        noteGroupType = noteGroupType,
                        searchText = searchText
                    ).rows
                }
                serviceAndDb(instruments_response)

            } catch (throwable: Throwable) {
                Log.d(TAG, throwable.message.toString())
            }
        }

        val cashed_instruments = instrumentsDao.returnOrderedInstrumentsQuery(
            instrumentId = instrumentId,
            instrumentGroupName = instrumentGroupName,
            searchText = searchText,
            page = page + 1,
            noteGroupType = noteGroupType
        )
        emit(Resource.success(data = cashed_instruments))

    }.catch { e ->
        emit(Resource.error(e))
    }
}