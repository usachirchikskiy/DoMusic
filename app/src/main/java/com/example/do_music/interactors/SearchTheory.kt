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
import android.os.Environment
import com.example.do_music.data.home.favourites.FavouritesDao
import com.example.do_music.util.DownloadStatus
import java.io.*


class SearchTheory(
    private val service: OpenMainApiService,
    private val theoryDao: TheoryDao,
    private val favouritesDao: FavouritesDao
) {
    private val TAG: String = "SearchTheory"
    private lateinit var context: Context

    fun execute(
        searchText: String,
        bookType: String,
        page: Int
    ): Flow<Resource<List<TheoryInfo>>> = flow {
        emit(Resource.loading<List<TheoryInfo>>())

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
            val list_of_fav_ids = mutableListOf<Int>()
            val books_favourite_response = service.getFavouriteItems(
                pageNumber = 0,
                docType = "BOOK"
            )
            var books_favourite_response_rows = books_favourite_response.rows
            favouritesDao.insertFavourite(books_favourite_response_rows)
            for (i in books_favourite_response_rows) {
                try {
                    i.bookId?.let { theoryDao.updateBook(it, true) }
                    i.bookId?.let { list_of_fav_ids.add(it) }
                } catch (throwable: Throwable) {
                    val getBook = i.bookId?.let { service.getBookById(it) }
                    getBook?.let { theoryDao.insertBook(it.copy(isFavourite = true)) }
                    Log.d(TAG, "execute: " + throwable.message)
                }
            }


            val total_page: Int = books_favourite_response.total / 10
            for (i in 1..total_page) {
                books_favourite_response_rows = service.getFavouriteItems(
                    pageNumber = i,
                    docType = "BOOK"
                ).rows
                favouritesDao.insertFavourite(books_favourite_response_rows)
                for (j in books_favourite_response_rows) {
                    try {
                        j.bookId?.let { theoryDao.updateBook(it, true) }
                        j.bookId?.let { list_of_fav_ids.add(it) }
                    } catch (throwable: Throwable) {
                        val getBook = j.bookId?.let { service.getBookById(it) }
                        getBook?.let { theoryDao.insertBook(it.copy(isFavourite = true)) }
                        Log.d(TAG, "execute: " + throwable.message + getBook)
                    }
                }
            }
            theoryDao.updateBooksFalse(list_of_fav_ids)
            var cashed_books = theoryDao.returnOrderedBooksQuery(
                page = page + 1,
                bookType = bookType,
                searchText = searchText
            )

            emit(Resource.success(data = cashed_books))
        } catch (throwable: Throwable) {
            Log.d("Error", throwable.message.toString())
            emit(
                Resource.error<List<TheoryInfo>>(throwable)
            )
        }
    }
}