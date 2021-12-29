package com.example.do_music.data.home.favourites

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.model.Favourite
import com.example.do_music.model.Instrument
import retrofit2.http.DELETE
import androidx.room.Delete

@Dao
interface FavouritesDao {

    suspend fun getFavItems(
        page: Int,
        pageSize: Int = 10,
        searchText: String,
        favoriteClass: String,
        docType: String
    ): List<Favourite> {
        var defaultOrFavouriteClass = ""
        if (favoriteClass != "UNKNOWN") defaultOrFavouriteClass = favoriteClass
        when (docType) {
            "NOTES" -> {
                return getFavNotes(page, pageSize, searchText, defaultOrFavouriteClass)
            }
            "BOOK" -> {

                return getFavBooks(page, pageSize, searchText, defaultOrFavouriteClass)
            }
            else ->
                return getFavVocals(page, pageSize, searchText, defaultOrFavouriteClass)
        }
    }

    @Query(
        """
        SElECT *FROM favourites WHERE
        (noteName  LIKE '%' || :searchText || '%'
        OR compositorName LIKE '%' || :searchText || '%')
        AND (noteId is not null
        AND notesClass LIKE '%' || :noteClass || '%')
        LIMIT (:page * :pageSize)
        """
    )
    suspend fun getFavNotes(
        page: Int,
        pageSize: Int,
        searchText: String,
        noteClass: String
    ): List<Favourite>


    @Query(
        """
        SElECT *FROM favourites WHERE
       (noteName  LIKE '%' || :searchText || '%'
        OR compositorName LIKE '%' || :searchText || '%')
        AND (vocalsId is not null
        AND vocalsClass LIKE '%' || :vocalsClass || '%')
        LIMIT (:page * :pageSize)
        """
    )
    suspend fun getFavVocals(
        page: Int,
        pageSize: Int,
        searchText: String,
        vocalsClass: String
    ): List<Favourite>

    @Query(
        """
        SElECT *FROM favourites WHERE
        (noteName  LIKE '%' || :searchText || '%'
        OR compositorName LIKE '%' || :searchText || '%')
        AND ( bookId is not null
        AND bookClass LIKE '%' || :bookClass || '%')
        LIMIT (:page * :pageSize)
        """
    )
    suspend fun getFavBooks(
        page: Int,
        pageSize: Int,
        searchText: String,
        bookClass: String
    ): List<Favourite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: List<Favourite>)


    @Query("DELETE FROM favourites WHERE favoriteId = :favouriteId")
    suspend fun deleteFavourite(favouriteId: Int)

    @Query("UPDATE favourites SET notesClass =:notesClass WHERE favoriteId=:favoriteId")
    suspend fun addFavNoteClassDao(notesClass: String, favoriteId: Int)

    @Query("UPDATE favourites SET bookClass =:bookClass WHERE favoriteId=:favoriteId")
    suspend fun addFavBookClassDao(bookClass: String, favoriteId: Int)

    @Query("UPDATE favourites SET vocalsClass =:vocalsClass WHERE favoriteId=:favoriteId")
    suspend fun addFavVocalsClassDao(vocalsClass: String, favoriteId: Int)

}