package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.vocal.VocalsDao
import com.example.do_music.model.Vocal
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchVocals(
    private val service: OpenMainApiService,
    private val vocalsDao: VocalsDao
) {
    private val TAG: String = "SearchVocals"

    fun execute(
        searchText: String,
        page: Int,
        update: Boolean
    ): Flow<Resource<List<Vocal>>> = flow {
        emit(Resource.loading<List<Vocal>>())
        if(!update) {
            try {
                // catch network exception
                var vocals_response = service.getVocals(
                    pageNumber = page,
                    searchText = searchText,
                ).rows

                for (vocal in vocals_response) {
                    try {
                        vocalsDao.insertVocal(vocal)
                    } catch (e: Exception) {
                        Log.d(TAG + " Error", e.message.toString())
                        e.printStackTrace()
                    }
                }


            } catch (throwable: Throwable) {
                Log.d("Error", throwable.message.toString())
                emit(
                    Resource.error<List<Vocal>>(throwable)
                )
            }
        }
        var cashed_vocals = vocalsDao.getAllBooks(
            page = page + 1,
            searchText = searchText
        )

        emit(Resource.success(data = cashed_vocals))
    }.catch { e ->
        emit(Resource.error(e))
    }
}