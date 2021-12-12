package com.example.do_music.data.home.instruments

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.do_music.model.Instrument
import com.example.do_music.util.Constants

private const val TAG = "InstrumentsDao"

@Dao
interface InstrumentsDao {

//    SELECT OrderID, Quantity,
//    CASE WHEN Quantity > 30 THEN 'The quantity is greater than 30'
//    WHEN Quantity = 30 THEN 'The quantity is 30'
//    ELSE 'The quantity is under 30'
//    END AS QuantityText
//    FROM OrderDetails


    @Query(
        """
        SELECT * FROM instruments 
        WHERE compositorId = :compositorId 
        AND noteName LIKE '%' || :searchText || '%'
        """
    )
    suspend fun getNotesByCompositor(compositorId: Int,searchText: String): List<Instrument>


    @Query(
        """
        SELECT * FROM instruments 
        WHERE compositorId = :compositorId AND 
        (concertsAndFantasies = :concertAndFantasies 
        OR sonatas = :ensembles) AND noteName LIKE '%' || :searchText || '%'
        """
    )
    suspend fun getNotesByCompositorAndNoteGroup(
        compositorId: Int,
        concertAndFantasies: String,
        ensembles: String,
        searchText: String
    ): List<Instrument>


    @Query(
        """
        UPDATE instruments SET 
        isFavourite = :isFavourite 
        WHERE noteId = :noteId
        """
    )
    suspend fun updateInstrument(noteId: Int, isFavourite: Boolean = true)


    @Query(
        """
        UPDATE instruments SET 
        isFavourite = :isFavourite 
        WHERE noteId NOT IN (:noteIds)
        """
    )
    suspend fun updateInstrumentFalse(noteIds: List<Int>, isFavourite: Boolean = false)


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

//    @Query(
//        """
//    SELECT * FROM instruments
//    WHERE isFavourite == :isFav
//    """
//    )
//
//    suspend fun getFav(isFav:Boolean = true): List<Instrument>


    @Query(
        """
    SELECT * FROM instruments 
    WHERE (noteName LIKE '%' || :searchText || '%'
    OR instrumentName  LIKE '%' || :searchText || '%')
    AND instrumentGroupName = :instrumentGroupName
    ORDER BY noteId DESC
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
    ORDER BY noteId DESC
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
    ORDER BY noteId DESC
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

//    @Query("SELECT noteId FROM instruments WHERE noteId = :id LIMIT 1")
//    suspend fun getItemId(id: Int): Int?
//
//
//    suspend fun insertOrUpdate(item: Instrument) {
//        database.runInTransaction {
//            val id = getItemDao().getItemId(item.id)
//
//            if(id == null)
//                getItemDao().insert(item)
//            else
//                getItemDao().update(item)
//        }
//    }

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
    Log.d(
        TAG, "instrgroupname: " + instrumentGroupName + "\n" +
                "ansamble " + noteGroupType + "\n" +
                "instumentId " + instrumentId + "\n" +
                "page" + page.toString() + "\n" +
                "searchText " + searchText + "\n"
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
        Log.d(TAG, "returnOrderedInstrumentsQuery: " + "instrumentId")
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
        Log.d(TAG, "returnOrderedInstrumentsQuery: " + "instrumentGroupName")
        return getInstrumentsByGroupId(
            instrumentGroupName = instrumentGroupName,
            page = page,
            searchText = searchText
        )
    }

    Log.d(TAG, "returnOrderedInstrumentsQuery: " + "getALL")
    return getAllInstrumentsSearch(searchText = searchText, page = page)

}

