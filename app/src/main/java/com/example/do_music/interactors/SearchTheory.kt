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
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream

class SearchTheory(
    private val service: OpenMainApiService,
    private val theoryDao: TheoryDao
) {
    private val TAG: String = "AppDebug"
    private lateinit var context: Context

    fun setContext(context: Context) {
        this.context = context
    }

    suspend fun downloadfile(uniqueName: String, fileName: String) {
        val responseBody = service.downloadFile(uniqueName)
        Log.d(
            TAG,
            "Name:" + uniqueName + "\nNAME_OF_FILE" + context.filesDir.absolutePath + fileName
        )
        saveFile(responseBody.body(), fileName)
    }


    fun saveFile(body: ResponseBody?, pathWhereYouWantToSaveFile: String): String {
        if (body == null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fos = FileOutputStream(pathWhereYouWantToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return pathWhereYouWantToSaveFile
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return ""
    }


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
//            Log.d(TAG, "\nSearchtext: " + searchText + "\nPage: "+page+"\nbookType"+bookType)
//            Log.d(TAG,"API RESPONSE")

            for (book in books_response) {
                try {
                    theoryDao.insertBook(book)
                } catch (e: Exception) {
                    Log.d(TAG + " Error", e.message.toString())
                    e.printStackTrace()
                }
            }

            val books_favourite_response = service.getFavouriteItems(
                pageNumber = 0,
                docType = "BOOK"
            )
            var books_favourite_response_rows = books_favourite_response.rows
            for (i in books_favourite_response_rows) {
                try {
                    i.bookId?.let { theoryDao.updateBook(it, true) }
                } catch (throwable: Throwable) {
                    val getBook = i.bookId?.let { service.getBookById(it) }
                    getBook?.let { theoryDao.insertBook(it) }
                    Log.d(TAG, "execute: " + throwable.message)
                }
            }


        val total_page: Int = books_favourite_response.total / 10
        for (i in 1..total_page) {
            books_favourite_response_rows = service.getFavouriteItems(
                pageNumber = i,
                docType = "BOOK"
            ).rows
            for (j in books_favourite_response_rows) {
                try {
                    j.bookId?.let { theoryDao.updateBook(it, true) }
                } catch (throwable: Throwable) {
                    val getBook = j.bookId?.let { service.getBookById(it) }
                    getBook?.let { theoryDao.insertBook(it) }
                    Log.d(TAG, "execute: " + throwable.message + getBook)
                }
//                j.bookId?.let { theoryDao.updateBook(it, true) }
            }
        }
//            Log.d(TAG,"DAO RESPONSE")

        var cashed_books = theoryDao.returnOrderedBooksQuery(
            page = page + 1,
            bookType = bookType,
            searchText = searchText
        )

        emit(Resource.success(data = cashed_books))
    } catch (throwable: Throwable)
    {
        Log.d("Error", throwable.message.toString())
        emit(
            Resource.error<List<TheoryInfo>>(throwable)
        )
    }
}
}