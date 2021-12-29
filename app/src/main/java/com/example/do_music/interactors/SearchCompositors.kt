package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.data.home.compositors.returnOrderedCompositorQuery
import com.example.do_music.model.CompositorEntity
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
    ): Flow<Resource<List<CompositorEntity>>> = flow {
        emit(Resource.loading<List<CompositorEntity>>())

        try {
            // catch network exception
            var compositors_response = service.returnOrderedCompositorQuery(
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

            var cashed_compositors = compositors.returnOrderedCompositorQuery(
                page = page + 1,
                country_filter = country_filter,
                searchText = searchText
            )

            emit(Resource.success(data = cashed_compositors))
        } catch (throwable: Throwable) {
            Log.d("Error", throwable.toString()+" " +throwable.message.toString())
            emit(
                Resource.error<List<CompositorEntity>>(throwable)
            )
        }
    }
}
