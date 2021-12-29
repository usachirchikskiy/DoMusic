package com.example.do_music.data.home.compositors

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.model.CompositorEntity
import com.example.do_music.util.Constants.Companion.PAGINATION_PAGE_SIZE

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
    ORDER BY name ASC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getAllCompositors(
        searchText: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<CompositorEntity>


    @Query(
        """
    SELECT * FROM compositors 
    WHERE name LIKE '%' || :searchText || '%'
    AND country LIKE '%' || :country_filter || '%' 
    ORDER BY name ASC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getCompositorsByCountry(
        country_filter: String,
        searchText: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<CompositorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompositor(compositor: CompositorEntity): Long

    @Query("DELETE FROM compositors")
    suspend fun deleteAllCompositors()

}

suspend fun CompositorsDao.returnOrderedCompositorQuery(
    country_filter: String,
    searchText: String,
    page: Int
): List<CompositorEntity> {

    when {
        country_filter != "" -> {
            return getCompositorsByCountry(
                country_filter = country_filter,
                searchText = searchText,
                page = page
            )
        }
        else -> {
            return getAllCompositors(
                searchText = searchText,
                page = page
            )

        }
    }
}
