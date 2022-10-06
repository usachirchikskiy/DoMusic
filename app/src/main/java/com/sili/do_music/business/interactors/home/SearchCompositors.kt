package com.sili.do_music.business.interactors.home

import com.sili.do_music.business.datasources.data.favourites.FavouritesDao
import com.sili.do_music.business.datasources.data.home.compositors.CompositorsDao
import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.theory.TheoryDao
import com.sili.do_music.business.datasources.data.home.vocal.VocalsDao
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.model.main.Compositor
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SearchCompositors(
    private val service: OpenMainApiService,
    private val compositors: CompositorsDao,
    private val favouritesDao: FavouritesDao,
    private val instrumentsDao: InstrumentsDao,
    private val vocalsDao: VocalsDao,
    private val theoryDao: TheoryDao
) {

    fun execute(
        searchText: String,
        country_filter: String,
        page: Int,
        isFirst: ()->Boolean
    ): Flow<Resource<List<Compositor>>> = flow {
        emit(Resource.loading())

        try {
            // catch network exception
            var compositorsResponse = service.getCompositors(
                pageNumber = page,
                searchText = searchText,
                country = country_filter
            ).rows

            if(isFirst()){
                favouritesDao.deleteAllFavourites()
                compositors.deleteAllCompositors()
                theoryDao.deleteAllBooks()
                instrumentsDao.deleteAllInstruments()
                vocalsDao.deleteAllVocals()
            }

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
                Resource.error(throwable)
            )
        }
        var cashedCompositors = compositors.getCompositors(
            page = page + 1,
            country_filter = country_filter,
            searchText = searchText
        )

        emit(Resource.success(data = cashedCompositors))
    }
}
