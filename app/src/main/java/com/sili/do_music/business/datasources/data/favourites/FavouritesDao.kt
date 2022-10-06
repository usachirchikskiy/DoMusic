package com.sili.do_music.business.datasources.data.favourites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sili.do_music.business.model.main.Favourite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {

    fun getFavItems(
        page: Int,
        pageSize: Int = 10,
        searchText: String,
        docType: String
    ): Flow<List<Favourite>> {
        return when (docType) {
            "NOTES" -> {
                getFavNotes(page, pageSize, searchText)
            }
            "BOOK" -> {
                getFavBooks(page, pageSize, searchText)
            }
            else ->
                getFavVocals(page, pageSize, searchText)
        }
    }

    @Query(
        """
        SElECT *FROM favourites WHERE
        (noteName  LIKE '%' || :searchText || '%'
        OR compositorName LIKE '%' || :searchText || '%')
        AND (noteId is not null)
        LIMIT (:page * :pageSize)
        """
    )
    fun getFavNotes(
        page: Int,
        pageSize: Int,
        searchText: String
    ): Flow<List<Favourite>>


    @Query(
        """
        SElECT *FROM favourites WHERE
       (noteName  LIKE '%' || :searchText || '%'
        OR compositorName LIKE '%' || :searchText || '%')
        AND (vocalsId is not null)
        LIMIT (:page * :pageSize)
        """
    )
    fun getFavVocals(
        page: Int,
        pageSize: Int,
        searchText: String
    ): Flow<List<Favourite>>

    @Query(
        """
        SElECT *FROM favourites WHERE
        (noteName  LIKE '%' || :searchText || '%'
        OR compositorName LIKE '%' || :searchText || '%')
        AND (bookId is not null)
        LIMIT (:page * :pageSize)
        """
    )
    fun getFavBooks(
        page: Int,
        pageSize: Int,
        searchText: String
    ): Flow<List<Favourite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteList(favourite: List<Favourite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteItem(favourite:Favourite)

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