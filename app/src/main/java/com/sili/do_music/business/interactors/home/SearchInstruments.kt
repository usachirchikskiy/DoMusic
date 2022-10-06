package com.sili.do_music.business.interactors.home

import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.instruments.returnOrderedInstrumentsQuery
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.datasources.network.main.home.InstrumentByGroup
import com.sili.do_music.business.model.main.Instrument
import com.sili.do_music.util.Constants.Companion.LAST_PAGE
import com.sili.do_music.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class SearchInstruments(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao
) {
    private suspend fun serviceAndDb(notes_response: List<Instrument>) =
        withContext(Dispatchers.IO) {
            if (notes_response.isNotEmpty()) {
                for (note in notes_response) {
                    instrumentsDao.insertInstrument(note)
                }
            } else {
                throw Exception(LAST_PAGE)
            }
        }

    fun getGroupOfInstruments(instrumentGroupName: String): Flow<Resource<List<InstrumentByGroup>>> =
        flow {
            try {
                val groups = service.getInstrumentsByGroupName(instrumentGroupName)
                emit(Resource.success(data = groups))
            } catch (throwable: Throwable) {
                emit(
                    Resource.error(throwable)
                )
            }
        }

    fun execute(
        instrumentId: Int = -1,
        instrumentGroupName: String = "",
        noteGroupType: String = "",
        searchText: String = " ",
        page: Int
    ): Flow<Resource<List<Instrument>>> = flow {
        emit(Resource.loading())

        lateinit var instrumentsResponse: List<Instrument>

        try {
            if (instrumentId != -1) {
                instrumentsResponse = service.getInstruments(
                    pageNumber = page,
                    instrumentType = instrumentGroupName,
                    noteGroupType = noteGroupType,
                    instrument = instrumentId,
                    searchText = searchText
                ).rows
            } else {
                instrumentsResponse = service.getInstruments(
                    pageNumber = page,
                    instrumentType = instrumentGroupName,
                    noteGroupType = noteGroupType,
                    searchText = searchText
                ).rows
            }
            serviceAndDb(instrumentsResponse)

        } catch (throwable: Throwable) {
            emit(Resource.error<List<Instrument>>(throwable))
        }

        val cashedInstruments = instrumentsDao.returnOrderedInstrumentsQuery(
            instrumentId = instrumentId,
            instrumentGroupName = instrumentGroupName,
            searchText = searchText,
            page = page + 1,
            noteGroupType = noteGroupType
        )

        cashedInstruments.collect {
            emit(Resource.success(data = it))
        }
    }
}