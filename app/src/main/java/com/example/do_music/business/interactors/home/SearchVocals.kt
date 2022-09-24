package com.example.do_music.business.interactors.home

import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.model.main.Vocal
import com.example.do_music.util.Constants.Companion.LAST_PAGE
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class SearchVocals(
    private val service: OpenMainApiService,
    private val vocalsDao: VocalsDao
) {

    fun execute(
        searchText: String,
        page: Int
    ): Flow<Resource<List<Vocal>>> = flow {
        emit(Resource.loading())

        try {
            // catch network exception
            val vocalsResponse = service.getVocals(
                pageNumber = page,
                searchText = searchText,
            ).rows
            if (vocalsResponse.isNotEmpty()) {
                for (vocal in vocalsResponse) {
                    vocalsDao.insertVocal(vocal)
                }
            } else {
                throw Exception(LAST_PAGE)
            }

        } catch (throwable: Throwable) {
            emit(Resource.error<List<Vocal>>(throwable))
        }

        val cashedVocals = vocalsDao.getAllVocals(
            page = page + 1,
            searchText = searchText
        )
        cashedVocals.collect {
            emit(Resource.success(data = it))
        }

    }
}