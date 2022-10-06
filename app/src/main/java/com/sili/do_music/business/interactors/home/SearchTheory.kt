package com.sili.do_music.business.interactors.home

import com.sili.do_music.business.datasources.data.home.theory.TheoryDao
import com.sili.do_music.business.datasources.data.home.theory.returnOrderedBooksQuery
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.model.main.TheoryInfo
import com.sili.do_music.util.Constants.Companion.LAST_PAGE
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.*


class SearchTheory(
    private val service: OpenMainApiService,
    private val theoryDao: TheoryDao
) {

    fun execute(
        searchText: String,
        bookType: String,
        page: Int
    ): Flow<Resource<List<TheoryInfo>>> = flow {
        try {
            emit(Resource.loading())
            val booksResponse = service.getBooks(
                pageNumber = page,
                searchText = searchText,
                bookType = bookType
            ).rows

            if (booksResponse.isNotEmpty()) {
                for (book in booksResponse) {
                    theoryDao.insertBook(book)
                }
            } else {
                throw Exception(LAST_PAGE)
            }

        }catch(e:Throwable){
            emit(Resource.error(e))
        }

        val cashedBooks = theoryDao.returnOrderedBooksQuery(
            page = page + 1,
            bookType = bookType,
            searchText = searchText
        )

        cashedBooks.collect {
            emit(Resource.success(it))
        }

    }

}

