package com.example.do_music.business.datasources.data.favourites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.business.model.main.Favourite

@Dao
interface FavouritesDao {

    suspend fun getFavItems(
        page: Int,
        pageSize: Int = 10,
        searchText: String,
        docType: String
    ): List<Favourite> {
        val defaultOrFavouriteClass = ""
        return when (docType) {
            "NOTES" -> {
                getFavNotes(page, pageSize, searchText, defaultOrFavouriteClass)
            }
            "BOOK" -> {
                getFavBooks(page, pageSize, searchText, defaultOrFavouriteClass)
            }
            else ->
                getFavVocals(page, pageSize, searchText, defaultOrFavouriteClass)
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

    @Query("DELETE FROM favourites")
    suspend fun deleteAllFavourites()

    @Query("DELETE FROM favourites WHERE favoriteId = :favouriteId")
    suspend fun deleteFavourite(favouriteId: Int)

//    @Query("UPDATE favourites SET notesClass =:notesClass WHERE favoriteId=:favoriteId")
//    suspend fun addFavNoteClassDao(notesClass: String, favoriteId: Int)
//
//    @Query("UPDATE favourites SET bookClass =:bookClass WHERE favoriteId=:favoriteId")
//    suspend fun addFavBookClassDao(bookClass: String, favoriteId: Int)
//
//    @Query("UPDATE favourites SET vocalsClass =:vocalsClass WHERE favoriteId=:favoriteId")
//    suspend fun addFavVocalsClassDao(vocalsClass: String, favoriteId: Int)

}