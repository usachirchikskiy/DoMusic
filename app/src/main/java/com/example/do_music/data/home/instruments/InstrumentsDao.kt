package com.example.do_music.data.home.instruments

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.model.Instrument
import com.example.do_music.model.TheoryInfo
import com.example.do_music.util.Constants

private const val TAG = "InstrumentsDao"
@Dao
interface InstrumentsDao {

    @Query(
        """
        UPDATE instruments SET isFavourite = :isFavourite
        WHERE noteId = :noteId
        """
    )
    suspend fun updateInstrument(noteId: Int, isFavourite: Boolean)


    @Query(
        """
    SELECT * FROM instruments
    WHERE noteName LIKE '%' || :searchText || '%'
    OR instrumentName  LIKE '%' || :searchText || '%'
    ORDER BY noteId DESC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getAllInstrumentsSearch(
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<Instrument>

    @Query(
        """
    SELECT * FROM instruments
    ORDER BY noteId DESC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getAllInstruments(
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<Instrument>

    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR instrumentName  LIKE '%' || :searchText || '%')
    AND instrumentGroupName = :instrumentGroupName
    ORDER BY noteName ASC
    LIMIT (:page * :pageSize)
    """
    )

    suspend fun getInstrumentsByGroupId(
        instrumentGroupName: String,
        searchText: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<Instrument>

    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR instrumentName  LIKE '%' || :searchText || '%')
    AND (instrumentGroupName = :instrumentGroupName
    AND (ensembles= :ensembles 
    OR introductionsAndVariations = :introductionsAndVariations
    OR concertsAndFantasies = :concertsAndFantasies
    OR playsAndSolos = :playsAndSolos
    OR sonatas = :sonatas
    OR studiesAndExercises = :studiesAndExercises)
    AND instrumentId = :instrumentId)
    LIMIT (:page * :pageSize)
    
    """
    )
    suspend fun getInstrumentsByInstrumentId(
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
    ): List<Instrument>
//    ORDER BY noteName ASC
//    LIMIT (:page * :pageSize)

    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR instrumentName  LIKE '%' || :searchText || '%')
    AND (instrumentGroupName = :instrumentGroupName
    AND (ensembles= :ensembles 
    OR introductionsAndVariations = :introductionsAndVariations
    OR concertsAndFantasies = :concertsAndFantasies
    OR playsAndSolos = :playsAndSolos
    OR sonatas = :sonatas
    OR studiesAndExercises = :studiesAndExercises
))
    ORDER BY noteName ASC
    LIMIT (:page * :pageSize)
    """
    )
    suspend fun getInstrumentsByEnsembles(
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
    ): List<Instrument>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstrument(instrument: Instrument): Long

    @Query("DELETE FROM instruments")
    suspend fun deleteAllInstruments()
}

suspend fun InstrumentsDao.returnOrderedInstrumentsQuery(
    noteGroupType: String,
    instrumentId: Int,
    instrumentGroupName: String,
    searchText: String,
    page: Int
): List<Instrument> {
    var ensembles = "-1"
    var introductionsAndVariations = "-1"
    var concertsAndFantasies = "-1"
    var playsAndSolos = "-1"
    var sonatas = "-1"
    var studiesAndExercises = "-1"
    Log.d(TAG, "instrgroupname: " + instrumentGroupName +"\n"+
            "ansamble "+ noteGroupType + "\n"+
            "instumentId " + instrumentId + "\n"+
            "page" +page.toString()+"\n"+
            "searchText "+ searchText+"\n"
    )
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

    if (instrumentId == -1 && noteGroupType!="") {

        Log.d(TAG, "returnOrderedInstrumentsQuery: " + "HERE3")
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
        Log.d(TAG, "returnOrderedInstrumentsQuery: " + "HERE2")
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
    }

    if (searchText!=" "){
        Log.d(TAG, "returnOrderedInstrumentsQuery: " + "HERE1")
        return getAllInstrumentsSearch(searchText = searchText, page = page)
    }
    Log.d(TAG, "returnOrderedInstrumentsQuery: " + "HERE")
    return getAllInstruments(page=page)


}

