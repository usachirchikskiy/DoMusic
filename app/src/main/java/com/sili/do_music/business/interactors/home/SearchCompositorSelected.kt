package com.sili.do_music.business.interactors.home

import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.vocal.VocalsDao
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.model.main.Instrument
import com.sili.do_music.business.model.main.Vocal
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow


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
        searchText: String
    ): Flow<Resource<List<Instrument>>> = flow {
        try {
            emit(Resource.loading())
            val instrumentalNotes = service.getInstrumentalNotesByCompositor(
                compositorId = compositorId,
                pageNumber = pageNumber,
                searchText = searchText
            ).rows

            if (instrumentalNotes.isNotEmpty()) {
                for (instrumentalNote in instrumentalNotes) {
                    instrumentsDao.insertInstrument(instrumentalNote)
                }
            } else {
                throw Exception(Constants.LAST_PAGE)
            }

        } catch (e: Throwable) {
            emit(Resource.error(e))
        }

        val cashedInstrumentalNotes = instrumentsDao.getInstrumentalNotesByCompositor(
            compositorId = compositorId,
            searchText = searchText,
            page = pageNumber + 1
        )

        cashedInstrumentalNotes.collect {
            emit(Resource.success(data = it))
        }
    }

    fun getVocalNotesByCompositors(
        compositorId: Int,
        pageNumber: Int,
        searchText: String
    ): Flow<Resource<List<Vocal>>> = flow {
        try {

            emit(Resource.loading())
            val vocalNotes = service.getVocalNotesByCompositor(
                compositorId = compositorId,
                pageNumber = pageNumber,
                searchText = searchText
            ).rows

            if (vocalNotes.isNotEmpty()) {
                for (vocalNote in vocalNotes) {
                    vocalsDao.insertVocal(vocalNote)
                }
            } else {
                throw Exception(Constants.LAST_PAGE)
            }

            val cashedVocalNotes = vocalsDao.getVocalNotesByCompositor(
                compositorId = compositorId,
                searchText = searchText,
                page = pageNumber + 1
            )

            cashedVocalNotes.collect{
                emit(Resource.success(data = it))
            }

        } catch (throwable: Throwable) {
            emit(Resource.error(throwable))
        }
    }
}