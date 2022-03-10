package com.example.do_music.business.datasources.data.home.theory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.business.model.main.TheoryInfo
import com.example.do_music.util.Constants

@Dao
interface TheoryDao {
    @Query(
        """
    SELECT favoriteId FROM theory_and_literature 
    WHERE bookId =:bookId
    """
    )
    suspend fun getFavouriteId(bookId: Int):Int

    @Query(
        """
        UPDATE theory_and_literature SET favoriteId = :favoriteId, favorite = :favorite
        WHERE bookId = :bookId
        """
    )
    suspend fun updateBook(favoriteId: Int?, favorite: Boolean, bookId: Int)

    @Query(
        """
        UPDATE theory_and_literature SET favoriteId = null, favorite = :favorite
        WHERE favoriteId = :favoriteId
        """
    )
    suspend fun updateBookToFalse(favoriteId: Int?, favorite: Boolean)

    @Query(
        """
    SELECT * FROM theory_and_literature 
    WHERE bookId =:bookId
    """
    )
    suspend fun getBook(
        bookId: Int
    ): TheoryInfo

    @Query(
        """
    SELECT * FROM theory_and_literature 
    WHERE bookName LIKE '%' || :searchText || '%'
    OR authorName  LIKE '%' || :searchText || '%'
    ORDER BY bookId DESC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getAllBooks(
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<TheoryInfo>

    @Query(
        """
    SELECT * FROM theory_and_literature 
    WHERE (bookName LIKE '%' || :searchText || '%'
    OR authorName  LIKE '%' || :searchText || '%')
    AND bookType LIKE '%' || :bookType || '%'
    ORDER BY bookId DESC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getBooksByType(
        bookType: String,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<TheoryInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: TheoryInfo): Long

    @Query("DELETE FROM theory_and_literature")
    suspend fun deleteAllBooks()

}

suspend fun TheoryDao.returnOrderedBooksQuery(
    bookType: String,
    searchText: String,
    page: Int
): List<TheoryInfo> {

    when {
        bookType != "" -> {
            return getBooksByType(
                bookType = bookType,
                searchText = searchText,
                page = page
            )
        }
        else -> {
            return getAllBooks(
                searchText = searchText,
                page = page
            )

        }
    }
}