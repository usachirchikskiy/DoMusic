package com.sili.do_music.business.datasources.data.home.instruments

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sili.do_music.business.model.main.Instrument
import com.sili.do_music.util.Constants
import kotlinx.coroutines.flow.Flow

private const val TAG = "InstrumentsDao"

@Dao
interface InstrumentsDao {

    @Query(
        """
    SELECT favoriteId FROM instruments 
    WHERE noteId =:noteId
    """
    )
    suspend fun getFavouriteId(noteId: Int): Int

    @Query("UPDATE instruments SET favoriteId = :favoriteId , favorite = :favorite WHERE noteId =:noteId")
    suspend fun instrumentUpdate(favoriteId: Int?, favorite: Boolean, noteId: Int)

    @Query(
        """
        UPDATE instruments SET favoriteId = null, favorite = :favorite
        WHERE favoriteId = :favoriteId
        """
    )
    suspend fun updateInstrumentToFalse(favoriteId: Int?, favorite: Boolean)

    @Query(
        """
        SELECT * FROM instruments 
        WHERE noteId = :noteId
        """
    )
    suspend fun getInstrument(noteId: Int): Instrument

    @Query(
        """
        SELECT * FROM instruments 
        WHERE compositorId = :compositorId 
        AND noteName LIKE '%' || :searchText || '%'
        ORDER BY noteId DESC
        LIMIT (:page * :pageSize)
        """
    )
    fun getInstrumentalNotesByCompositor(
        compositorId: Int,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): Flow<List<Instrument>>

    @Query(
        """
    SELECT * FROM instruments
    WHERE noteName LIKE '%' || :searchText || '%'
    OR compositorName  LIKE '%' || :searchText || '%'
    ORDER BY noteId DESC
    LIMIT (:page * :pageSize)
    """
    )
    fun getAllInstrumentsSearch(
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): Flow<List<Instrument>>

    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR compositorName  LIKE '%' || :searchText || '%')
    AND instrumentGroupName = :instrumentGroupName
    ORDER BY noteId DESC
    LIMIT (:page * :pageSize)
    """
    )
    fun getInstrumentsByGroupId(
        instrumentGroupName: String,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): Flow<List<Instrument>>

    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR compositorName  LIKE '%' || :searchText || '%')
    AND (instrumentGroupName = :instrumentGroupName
    AND (ensembles= :ensembles 
    OR introductionsAndVariations = :introductionsAndVariations
    OR concertsAndFantasies = :concertsAndFantasies
    OR playsAndSolos = :playsAndSolos
    OR sonatas = :sonatas
    OR studiesAndExercises = :studiesAndExercises)
    AND instrumentId = :instrumentId)
    ORDER BY noteId DESC
    LIMIT (:page * :pageSize)
    
    """
    )
    fun getInstrumentsByInstrumentId(
        ensembles: String,
        instrumentGroupName: String,
        introductionsAndVariations: String,
        concertsAndFantasies: String,
        playsAndSolos: String,
        sonatas: String,
        studiesAndExercises: String,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE,
        instrumentId: Int
    ): Flow<List<Instrument>>

    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR compositorName  LIKE '%' || :searchText || '%')
    AND (instrumentGroupName = :instrumentGroupName
    AND (ensembles= :ensembles 
    OR introductionsAndVariations = :introductionsAndVariations
    OR concertsAndFantasies = :concertsAndFantasies
    OR playsAndSolos = :playsAndSolos
    OR sonatas = :sonatas
    OR studiesAndExercises = :studiesAndExercises
))
    ORDER BY noteId DESC
    LIMIT (:page * :pageSize)
    """
    )
    fun getInstrumentsByEnsembles(
        ensembles: String,
        instrumentGroupName: String,
        introductionsAndVariations: String,
        concertsAndFantasies: String,
        playsAndSolos: String,
        sonatas: String,
        studiesAndExercises: String,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): Flow<List<Instrument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstrument(instrument: Instrument): Long

    @Query("DELETE FROM instruments")
    suspend fun deleteAllInstruments()

}

fun InstrumentsDao.returnOrderedInstrumentsQuery(
    noteGroupType: String,
    instrumentId: Int,
    instrumentGroupName: String,
    searchText: String,
    page: Int
): Flow<List<Instrument>> {
    var ensembles = "-1"
    var introductionsAndVariations = "-1"
    var concertsAndFantasies = "-1"
    var playsAndSolos = "-1"
    var sonatas = "-1"
    var studiesAndExercises = "-1"
    when (noteGroupType) {
        "ENSEMBLES" -> {
            ensembles = "1"
        }
        "INTRODUCTIONS_AND_VARIATIONS" -> {
            introductionsAndVariations = "1"

        }
        "CONCERTS_AND_FANTASIES" -> {
            concertsAndFantasies = "1"

        }
        "PLAYS_AND_SOLOS" -> {
            playsAndSolos = "1"

        }
        "SONATAS" -> {
            sonatas = "1"

        }
        "STUDIES_AND_EXERCISES" -> {
            studiesAndExercises = "1"
        }
    }

    if (instrumentId == -1 && noteGroupType != "") {

        Log.d(TAG, "returnOrderedInstrumentsQuery: " + "ByAnsamble")
        return getInstrumentsByEnsembles(
            ensembles = ensembles,
            instrumentGroupName = instrumentGroupName,
            introductionsAndVariations = introductionsAndVariations,
            concertsAndFantasies = concertsAndFantasies,
            playsAndSolos = playsAndSolos,
            sonatas = sonatas,
            studiesAndExercises = studiesAndExercises,
            searchText = searchText,
            page = page
        )
    } else if (instrumentId != -1) {
        return getInstrumentsByInstrumentId(
            instrumentId = instrumentId,
            ensembles = ensembles,
            instrumentGroupName = instrumentGroupName,
            introductionsAndVariations = introductionsAndVariations,
            concertsAndFantasies = concertsAndFantasies,
            playsAndSolos = playsAndSolos,
            sonatas = sonatas,
            studiesAndExercises = studiesAndExercises,
            searchText = searchText,
            page = page
        )
    } else if (instrumentGroupName != "") {
        return getInstrumentsByGroupId(
            instrumentGroupName = instrumentGroupName,
            page = page,
            searchText = searchText
        )
    }
    return getAllInstrumentsSearch(searchText = searchText, page = page)

}

