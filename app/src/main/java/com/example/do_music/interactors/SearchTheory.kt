package com.example.do_music.interactors

import android.content.Context
import android.util.Log
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.data.home.theory.returnOrderedBooksQuery
import com.example.do_music.model.TheoryInfo
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch


class SearchTheory(
    private val service: OpenMainApiService,
    private val theoryDao: TheoryDao
) {
    private val TAG: String = "SearchTheory"
    private lateinit var context: Context

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
                var books_response = service.getBooks(
                    pageNumber = page,
                    searchText = searchText,
                    bookType = bookType
                ).rows
                for (book in books_response) {
                    try {
                        theoryDao.insertBook(book)
                    } catch (e: Exception) {
                        Log.d(TAG + " Error", e.message.toString())
                        e.printStackTrace()
                    }
                }

            } catch (throwable: Throwable) {
                Log.d("Error", throwable.message.toString())
                emit(
                    Resource.error<List<TheoryInfo>>(throwable)
                )
            }
        }
        var cashed_books = theoryDao.returnOrderedBooksQuery(
            page = page + 1,
            bookType = bookType,
            searchText = searchText
        )

        emit(Resource.success(data = cashed_books))
    }.catch { e ->
        emit(Resource.error(e))
    }
}

