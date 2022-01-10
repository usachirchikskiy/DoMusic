package com.example.do_music.data.home.vocal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.model.TheoryInfo
import com.example.do_music.model.Vocal
import com.example.do_music.util.Constants


@Dao
interface VocalsDao {

    @Query(
        """
        UPDATE vocals SET favoriteId = :favoriteId, favorite = :favorite
        WHERE vocalsId = :vocalsId
        """
    )
    suspend fun updateVocal(favoriteId:Int?,favorite: Boolean,vocalsId: Int)

    @Query(
        """
        UPDATE vocals SET favoriteId = null, favorite = :favorite
        WHERE favoriteId = :favoriteId
        """
    )
    suspend fun updateVocalToFalse(favoriteId:Int?,favorite: Boolean)
//
//    @Query(
//        """
//        UPDATE vocals SET
//        isFavourite = :isFavourite
//        WHERE vocalsId NOT IN (:vocalsIds)
//        """
//    )
//    suspend fun updateVocalsFalse(vocalsIds: List<Int>, isFavourite: Boolean = false)

    @Query(
        """
    SELECT * FROM vocals
    WHERE noteName LIKE '%' || :searchText || '%'
    OR compositorName  LIKE '%' || :searchText || '%'
    ORDER BY vocalsId DESC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getAllBooks(
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<Vocal>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocal(vocal: Vocal): Long

    @Query(
        """
    SELECT * FROM vocals
    WHERE vocalsId =:vocalsId
    """
    )
    suspend fun getVocal(
        vocalsId: Int
    ): Vocal

}