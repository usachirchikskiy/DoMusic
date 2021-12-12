package com.example.do_music.data.home.theory

import androidx.room.*
import com.example.do_music.data.home.compositors.CompositorEntity
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.model.TheoryInfo
import com.example.do_music.util.Constants

@Dao
interface TheoryDao {

    @Query("""
        UPDATE theory_and_literature SET isFavourite = :isFavourite 
        WHERE bookId = :bookId
        """)
    suspend fun updateBook(bookId: Int, isFavourite: Boolean)

    @Query(
        """
        UPDATE theory_and_literature SET 
        isFavourite = :isFavourite 
        WHERE bookId NOT IN (:bookIds)
        """
    )
    suspend fun updateBooksFalse(bookIds: List<Int>, isFavourite: Boolean = false)

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