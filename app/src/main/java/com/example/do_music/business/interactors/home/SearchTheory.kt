package com.example.do_music.business.interactors.home

import android.content.Context
import android.util.Log
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.theory.returnOrderedBooksQuery
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.model.main.TheoryInfo
import com.example.do_music.util.Constants.Companion.LAST_PAGE
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class SearchTheory(
    private val service: OpenMainApiService,
    private val theoryDao: TheoryDao
) {
    private val TAG: String = "SearchTheory"

    fun execute(
        searchText: String,
        bookType: String,
        page: Int,
        update: Boolean
    ): Flow<Resource<List<TheoryInfo>>> = flow {
        emit(Resource.loading<List<TheoryInfo>>())
        if (!update) {
            try {
                // catch network exception
                val booksResponse = service.getBooks(
                    pageNumber = page,
                    searchText = searchText,
                    bookType = bookType
                ).rows
                if(booksResponse.isNotEmpty()) {
                    for (book in booksResponse) {
                        theoryDao.insertBook(book)
                    }
                }
                else{
                    throw Exception(LAST_PAGE)
                }

            } catch (throwable: Throwable) {
                emit(Resource.error<List<TheoryInfo>>(throwable))
            }
        }
        val cashedBooks = theoryDao.returnOrderedBooksQuery(
            page = page + 1,
            bookType = bookType,
            searchText = searchText
        )

        emit(Resource.success(data = cashedBooks))
    }.catch { e ->
        emit(Resource.error(e))
    }
}

