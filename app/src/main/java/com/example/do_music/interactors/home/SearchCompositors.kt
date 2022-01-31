package com.example.do_music.interactors.home

import android.util.Log
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.model.Compositor
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.*


class SearchCompositors(
    private val service: OpenMainApiService,
    private val compositors: CompositorsDao
) {

    private val TAG: String = "SearchCompositors"

    fun execute(
        searchText: String,
        country_filter: String,
        page: Int
    ): Flow<Resource<List<Compositor>>> = flow {
        emit(Resource.loading<List<Compositor>>())

        try {
            // catch network exception
            var compositors_response = service.getCompositors(
                pageNumber = page,
                searchText = searchText,
                country = country_filter
            ).rows

            for (compositor in compositors_response) {
                try {
                    compositors.insertCompositor(compositor)
                } catch (e: Exception) {
                    Log.d(TAG + " Error", e.message.toString())
                    e.printStackTrace()
                }
            }
        } catch (throwable: Throwable) {
            Log.d(TAG, throwable.toString()+" " +throwable.message.toString())
        }
        var cashed_compositors = compositors.getCompositors(
            page = page + 1,
            country_filter = country_filter,
            searchText = searchText
        )

        emit(Resource.success(data = cashed_compositors))
    }.catch { throwable->
        emit(
            Resource.error<List<Compositor>>(throwable)
        )
    }
}
