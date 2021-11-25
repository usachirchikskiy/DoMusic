package com.example.do_music.data.home.favourites

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

    @Query(
        """
        SElECT favoriteId FROM favourites WHERE (noteId = :noteId
        OR bookId == :bookId OR vocalsId == :vocalsId)
        LIMIT 1
        """
    )
    suspend fun getFavId(noteId: Int=-1,bookId:Int=-1,vocalsId:Int=-1): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: List<Favourite>)

    @Query("DELETE FROM favourites WHERE favoriteId == :favouriteId")
    suspend fun delete(favouriteId:Int)
}