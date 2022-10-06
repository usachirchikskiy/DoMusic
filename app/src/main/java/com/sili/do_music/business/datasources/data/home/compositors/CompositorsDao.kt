package com.sili.do_music.business.datasources.data.home.compositors

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sili.do_music.business.model.main.Compositor
import com.sili.do_music.util.Constants.Companion.PAGINATION_PAGE_SIZE

//
//SELECT * FROM blog_post
//WHERE title LIKE '%' || :query || '%'
//OR body LIKE '%' || :query || '%'
//OR username LIKE '%' || :query || '%'
//ORDER BY date_updated DESC LIMIT (:page * :pageSize)

@Dao
interface CompositorsDao {

    @Query(
        """
    SELECT * FROM compositors 
    WHERE name LIKE '%' || :searchText || '%'
    AND country LIKE '%' || :country_filter || '%' 
    ORDER BY name ASC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getCompositors(
        country_filter: String,
        searchText: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<Compositor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompositor(compositor: Compositor): Long

    @Query("DELETE FROM compositors")
    suspend fun deleteAllCompositors()

}

