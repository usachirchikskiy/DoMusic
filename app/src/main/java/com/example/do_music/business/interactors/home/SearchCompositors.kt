package com.example.do_music.business.interactors.home

import com.example.do_music.business.datasources.data.home.compositors.CompositorsDao
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.model.main.Compositor
import com.example.do_music.util.Constants
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class SearchCompositors(
    private val service: OpenMainApiService,
    private val compositors: CompositorsDao
) {

    fun execute(
        searchText: String,
        country_filter: String,
        page: Int
    ): Flow<Resource<List<Compositor>>> = flow {
        emit(Resource.loading<List<Compositor>>())

        try {
            // catch network exception
            var compositorsResponse = service.getCompositors(
                pageNumber = page,
                searchText = searchText,
                country = country_filter
            ).rows
            if (compositorsResponse.isNotEmpty()) {
                for (compositor in compositorsResponse) {
                    val name = compositor.name.replace("\\s+".toRegex(), " ").trim()
                    compositor.name = name
                    compositors.insertCompositor(compositor)
                }
            } else {
                throw Exception(Constants.LAST_PAGE)
            }

        } catch (throwable: Throwable) {
            emit(
                Resource.error<List<Compositor>>(throwable)
            )
        }
        var cashedCompositors = compositors.getCompositors(
            page = page + 1,
            country_filter = country_filter,
            searchText = searchText
        )

        emit(Resource.success(data = cashedCompositors))
    }.catch { throwable ->
        emit(
            Resource.error<List<Compositor>>(throwable)
        )
    }
}
