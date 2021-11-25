package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.data.home.compositors.returnOrderedCompositorQuery
import com.example.do_music.data.home.compositors.toCompositorInfo
import com.example.do_music.data.home.compositors.toEntity
import com.example.do_music.model.CompositorInfo
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.network.main.returnOrderedCompositorQuery
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.*


class SearchCompositors(
    private val service: OpenMainApiService,
    private val compositors: CompositorsDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        searchText: String,
        country_filter: String,
        page: Int
    ): Flow<Resource<List<CompositorInfo>>> = flow {
        emit(Resource.loading<List<CompositorInfo>>())

        try {
            // catch network exception
            var compositors_response = service.returnOrderedCompositorQuery(
                pageNumber = page,
                searchText = searchText,
                country = country_filter
            ).rows
//            Log.d(TAG, "\nSearchtext: " + searchText + "\nPage: "+page+"\nCountryFilter"+country_filter)
//            Log.d(TAG,"API RESPONSE")

            for (compositor in compositors_response) {
                try {
                    compositors.insertCompositor(compositor.toEntity())
                } catch (e: Exception) {
                    Log.d(TAG + " Error", e.message.toString())
                    e.printStackTrace()
                }
            }
//            Log.d(TAG,"DAO RESPONSE")

            var cashed_compositors = compositors.returnOrderedCompositorQuery(
                page = page + 1,
                country_filter = country_filter,
                searchText = searchText
            ).map {
                it.toCompositorInfo()
            }

            emit(Resource.success(data = cashed_compositors))
        } catch (throwable: Throwable) {
            Log.d("Error", throwable.message.toString())
            emit(
                Resource.error<List<CompositorInfo>>(throwable)
            )
        }
    }
}
