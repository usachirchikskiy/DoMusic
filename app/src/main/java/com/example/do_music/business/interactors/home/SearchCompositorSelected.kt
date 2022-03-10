package com.example.do_music.business.interactors.home

import android.util.Log
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.model.main.Instrument
import com.example.do_music.business.model.main.Vocal
import com.example.do_music.util.Constants
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

private const val TAG = "SelectedInteractor"

class SearchCompositorSelected(
    private val service: OpenMainApiService,
    private val instrumentsDao: InstrumentsDao,
    private val vocalsDao: VocalsDao
) {

    fun getNoteGroupTypes(
        compositorId: Int,
        pageNumber: Int = 0
    ): Flow<Resource<List<String>>> = flow {
        try {
            val listOfGroupTypes = arrayListOf<String>()
            val totalInstrumental = service.getInstrumentalNotesByCompositor(
                compositorId = compositorId,
                pageNumber = pageNumber
            ).total
            val totalVocal = service.getVocalNotesByCompositor(
                compositorId = compositorId,
                pageNumber = pageNumber
            ).total
            if (totalVocal > 0 && totalInstrumental > 0) {
                listOfGroupTypes.add(Constants.VOCAL_GROUP)
                listOfGroupTypes.add(Constants.INSTRUMENTAL_GROUP)
            } else if (totalVocal > 0) {
                listOfGroupTypes.add(Constants.VOCAL_GROUP)
            } else if (totalInstrumental > 0) {
                listOfGroupTypes.add(Constants.INSTRUMENTAL_GROUP)
            }
            emit(Resource.success(data = listOfGroupTypes.toList()))

        } catch (throwable: Throwable) {
            emit(Resource.error<List<String>>(throwable))
        }
    }

    fun getInstrumentalNotesByCompositors(
        compositorId: Int,
        pageNumber: Int,
        searchText: String,
        update: Boolean
    ): Flow<Resource<List<Instrument>>> = flow {

        try {
            if (!update) {
                emit(Resource.loading<List<Instrument>>())
                val instrumentalNotes = service.getInstrumentalNotesByCompositor(
                    compositorId = compositorId,
                    pageNumber = pageNumber,
                    searchText = searchText
                ).rows

                if(instrumentalNotes.isNotEmpty()) {
                    for (instrumentalNote in instrumentalNotes) {
                        instrumentsDao.insertInstrument(instrumentalNote)
                    }
                }
                else{
                    throw Exception(Constants.LAST_PAGE)
                }
            }
        } catch (throwable: Throwable) {
            emit(Resource.error<List<Instrument>>(throwable))
        }
        val cashedInstrumentalNotes = instrumentsDao.getInstrumentalNotesByCompositor(
            compositorId = compositorId,
            searchText = searchText,
            page = pageNumber + 1
        )
        emit(Resource.success(data = cashedInstrumentalNotes))
    }.catch { error ->
        emit(Resource.error(error))
    }

    fun getVocalNotesByCompositors(
        compositorId: Int,
        pageNumber: Int,
        searchText: String,
        update: Boolean
    ): Flow<Resource<List<Vocal>>> = flow {
        try {
            if (!update) {
                emit(Resource.loading<List<Vocal>>())
                val vocalNotes = service.getVocalNotesByCompositor(
                    compositorId = compositorId,
                    pageNumber = pageNumber,
                    searchText = searchText
                ).rows

                if(vocalNotes.isNotEmpty()){
                    for (vocalNote in vocalNotes) {
                        vocalsDao.insertVocal(vocalNote)
                    }
                }
                else{
                    throw Exception(Constants.LAST_PAGE)
                }
            }
        } catch (throwable: Throwable) {
            emit(Resource.error<List<Vocal>>(throwable))
        }
        val cashedVocalNotes = vocalsDao.getVocalNotesByCompositor(
            compositorId = compositorId,
            searchText = searchText,
            page = pageNumber + 1
        )
        emit(Resource.success(data = cashedVocalNotes))
    }.catch { error ->
        emit(Resource.error(error))
    }
}