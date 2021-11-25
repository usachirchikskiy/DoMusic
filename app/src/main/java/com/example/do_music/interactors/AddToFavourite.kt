package com.example.do_music.interactors

import android.util.Log
import com.example.do_music.data.home.favourites.FavouritesDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.favourites.FavouriteItem
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AddToFavourite(
    private val service: OpenMainApiService,
    private val favouritesDao: FavouritesDao
) {
    private val TAG: String = "AppDebug"

    fun execute(
        noteId: Int = -1,
        bookId: Int = -1,
        vocalsId: Int = -1,
        isFavourite: Boolean
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading<String>())
        val body_request = JsonObject()
        var addApi: FavouriteItem
        try {

            var docType = ""
            var property = ""
            var id:Int = -1
            bookId.let {
                docType = "BOOK"
                property = "bookId"
                id = bookId
            }
            noteId.let {
                docType = "NOTES"
                property = "noteId"
                id = noteId
            }
            vocalsId.let {
                docType = "VOCALS"
                property = "vocalsId"
                id = vocalsId
            }
                    when {
                    isFavourite->{
                        body_request.addProperty(property, id)
                        addApi = service.addtoFavourite(body_request.toString())
                        var favourites = service.getFavouriteItems(
                            pageNumber = 0,
                            docType = docType
                        )
                        favouritesDao.insertFavourite(favourite = favourites.rows)
                        val total_page:Int = favourites.total/10
                        for(i in 1..total_page){
                            favourites = service.getFavouriteItems(
                                pageNumber = i,
                                docType = docType
                            )
                            favouritesDao.insertFavourite(favourite = favourites.rows)
                        }
                    }
                    else->{
                        val removeId = favouritesDao.getFavId(noteId,bookId, vocalsId)
                        val response = service.removeFromFavourites(removeId)
                        Log.d(TAG, "Deleted: " + response)
                        favouritesDao.delete(removeId)
                    }
            }
            emit(Resource.success("Updated"))
        } catch (throwable: Throwable) {
            Log.d("Error", throwable.message.toString())
            emit(
                Resource.error<String>(throwable)
            )
        }

    }
}