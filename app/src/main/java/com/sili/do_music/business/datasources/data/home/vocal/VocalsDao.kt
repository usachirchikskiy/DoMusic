package com.sili.do_music.business.datasources.data.home.vocal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sili.do_music.business.model.main.Vocal
import com.sili.do_music.util.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface VocalsDao {

    @Query(
        """
    SELECT favoriteId FROM vocals 
    WHERE vocalsId =:vocalsId
    """
    )
    suspend fun getFavouriteId(vocalsId: Int): Int

    @Query(
        """
        UPDATE vocals SET favoriteId = :favoriteId, favorite = :favorite
        WHERE vocalsId = :vocalsId
        """
    )
    suspend fun updateVocal(favoriteId: Int?, favorite: Boolean, vocalsId: Int)

    @Query(
        """
        UPDATE vocals SET favoriteId = null, favorite = :favorite
        WHERE favoriteId = :favoriteId
        """
    )
    suspend fun updateVocalToFalse(favoriteId: Int?, favorite: Boolean)

    @Query(
        """
        SELECT * FROM vocals 
        WHERE compositorId = :compositorId 
        AND noteName LIKE '%' || :searchText || '%'
        ORDER BY vocalsId DESC
        LIMIT (:page * :pageSize)
        """
    )
    fun getVocalNotesByCompositor(
        compositorId: Int,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): Flow<List<Vocal>>

    @Query(
        """
    SELECT * FROM vocals
    WHERE noteName LIKE '%' || :searchText || '%'
    OR compositorName  LIKE '%' || :searchText || '%'
    ORDER BY vocalsId DESC
    LIMIT (:page * :pageSize)
    """
    )
    fun getAllVocals(
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): Flow<List<Vocal>>


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

    @Query("DELETE FROM vocals")
    suspend fun deleteAllVocals()

}