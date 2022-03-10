package com.example.do_music.business.interactors.home

import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.instruments.returnOrderedInstrumentsQuery
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.datasources.network.main.home.InstrumentByGroup
import com.example.do_music.business.model.main.Instrument
import com.example.do_music.util.Constants.Companion.LAST_PAGE
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
    private suspend fun serviceAndDb(notes_response: List<Instrument>) =
        withContext(Dispatchers.IO) {
            if (notes_response.isNotEmpty()) {
                for (note in notes_response) {
                    instrumentsDao.insertInstrument(note)
                }
            }
            else{
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
        emit(Resource.loading())

        lateinit var instrumentsResponse: List<Instrument>
        if (!update) {
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
        }

        val cashedInstruments = instrumentsDao.returnOrderedInstrumentsQuery(
            instrumentId = instrumentId,
            instrumentGroupName = instrumentGroupName,
            searchText = searchText,
            page = page + 1,
            noteGroupType = noteGroupType
        )
        emit(Resource.success(data = cashedInstruments))

    }.catch { e ->
        emit(Resource.error(e))
    }
}