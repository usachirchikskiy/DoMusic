package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.favourites.FavouritesDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.instruments.returnOrderedInstrumentsQuery
import com.example.do_music.model.Instrument
import com.example.do_music.network.main.InstrumentByGroup
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

private const val TAG = "SearchInstruments"

class SearchInstruments(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao,
    private val favouritesDao: FavouritesDao
) {


    suspend fun serviceAndDb(notes_response:List<Instrument>) = withContext(Dispatchers.IO){
        for (note in notes_response) {
            try {
                instrumentsDao.insertInstrument(note)
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                e.printStackTrace()
            }
        }

        val list_of_fav_ids = mutableListOf<Int>()
        val instruments_favourite_response = service.getFavouriteItems(
            pageNumber = 0,
            docType = "NOTES"
        )
        var instruments_favourite_response_rows = instruments_favourite_response.rows
        favouritesDao.insertFavourite(instruments_favourite_response_rows)
        for (i in instruments_favourite_response_rows) {
            try {
                i.noteId?.let { list_of_fav_ids.add(it) }
                i.noteId?.let { instrumentsDao.updateInstrument(it) }
            } catch (throwable: Throwable) {
                val getInstrument = i.noteId?.let { service.getInstrumentById(it) }
                getInstrument?.let { instrumentsDao.insertInstrument(it.copy(isFavourite = true)) }
                Log.d(TAG, "execute:First " + throwable.message)
            }
        }

        val total_page: Int = instruments_favourite_response.total / 10
        for (i in 1..total_page) {
            instruments_favourite_response_rows = service.getFavouriteItems(
                pageNumber = i,
                docType = "NOTES"
            ).rows
            favouritesDao.insertFavourite(instruments_favourite_response_rows)
            for (j in instruments_favourite_response_rows) {
                try {
                    j.noteId?.let { list_of_fav_ids.add(it) }
                    j.noteId?.let { instrumentsDao.updateInstrument(it) }
                } catch (throwable: Throwable) {
                    val getInstrument = j.noteId?.let { service.getInstrumentById(it) }
                    getInstrument?.let { instrumentsDao.insertInstrument(it.copy(isFavourite = true)) }
                    Log.d(TAG, "execute: " + throwable.message)
                }
            }
        }
        instrumentsDao.updateInstrumentFalse(list_of_fav_ids.toList())
    }

    fun executeCompositors(
        noteGroupType: String,
        searchText: String ,
        pageNumber: Int,
        compositorId: Int
    ): Flow<Resource<List<Instrument>>> = flow {
        emit(Resource.loading<List<Instrument>>())
        try {
            // catch network exception
            var notes_response = service.getNotesByCompositor(
                compositorId = compositorId,
                searchText = searchText,
                pageNumber = pageNumber,
                noteGroupType = noteGroupType
            ).rows

            serviceAndDb(notes_response)
            var concerts = -1
            var ansamble = -1
            if (noteGroupType=="CONCERTS_AND_FANTASIES"){
                concerts = 1
            }
            else if(noteGroupType=="SONATAS"){
                ansamble = 1
            }
            lateinit var cashed_instruments: List<Instrument>
            if(noteGroupType!="") cashed_instruments = instrumentsDao.getNotesByCompositorAndNoteGroup(
                compositorId,
                concertAndFantasies = concerts.toString(),
                ensembles = ansamble.toString(),
                searchText
            )
            else cashed_instruments = instrumentsDao.getNotesByCompositor(compositorId,searchText)

            Log.d(TAG, "Db:\n" + cashed_instruments.toString())
            emit(Resource.success(data = cashed_instruments))
        } catch (throwable: Throwable) {
            Log.d(TAG, throwable.toString()+"\n"+throwable.message.toString())
            emit(
                Resource.error<List<Instrument>>(throwable)
            )
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
        page: Int
    ): Flow<Resource<List<Instrument>>> = flow {
        emit(Resource.loading<List<Instrument>>())
        try {
            lateinit var instruments_response: List<Instrument>
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
            Log.d(TAG, throwable.message.toString())
            emit(
                Resource.error<List<Instrument>>(throwable)
            )

        }
    }
}