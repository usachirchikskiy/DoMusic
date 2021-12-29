package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.favourites.FavouritesDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.instruments.returnOrderedInstrumentsQuery
import com.example.do_music.main.ui.home.ui.itemSelected.ItemState
import com.example.do_music.model.Instrument
import com.example.do_music.network.main.InstrumentByGroup
import com.example.do_music.network.main.OpenMainApiService
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

    fun executeCompositors(
        noteGroupType: String,
        searchText: String,
        pageNumber: Int,
        compositorId: Int,
        update: Boolean
    ): Flow<Resource<List<Instrument>>> = flow {
        emit(Resource.loading<List<Instrument>>())
        if (!update) {
            try {
                // catch network exception
                var notes_response = service.getNotesByCompositor(
                    compositorId = compositorId,
                    searchText = searchText,
                    pageNumber = pageNumber,
                    noteGroupType = noteGroupType
                ).rows

                serviceAndDb(notes_response)

            } catch (throwable: Throwable) {
                Log.d(TAG, throwable.toString() + "\n" + throwable.message.toString())
                emit(
                    Resource.error<List<Instrument>>(throwable)
                )
            }
        }

        var concerts = -1
        var ansamble = -1
        if (noteGroupType == "CONCERTS_AND_FANTASIES") {
            concerts = 1
        } else if (noteGroupType == "SONATAS") {
            ansamble = 1
        }

        lateinit var cashed_instruments: List<Instrument>
        if (noteGroupType != "") cashed_instruments =
            instrumentsDao.getNotesByCompositorAndNoteGroup(
                compositorId,
                concertAndFantasies = concerts.toString(),
                ensembles = ansamble.toString(),
                searchText
            )
        else cashed_instruments = instrumentsDao.getNotesByCompositor(compositorId, searchText)

        Log.d(TAG, "Db:\n" + cashed_instruments.toString())
        emit(Resource.success(data = cashed_instruments))
    }.catch { e ->
        emit(Resource.error(e))
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
                emit(
                    Resource.error<List<Instrument>>(throwable)
                )
            }
        }

        //        Log.d(TAG, "Inet:\n" + instruments_response.toString())
        var cashed_instruments = instrumentsDao.returnOrderedInstrumentsQuery(
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