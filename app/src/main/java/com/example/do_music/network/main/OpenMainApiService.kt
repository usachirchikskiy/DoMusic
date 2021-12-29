package com.example.do_music.network.main

import com.example.do_music.data.home.favourites.FavouriteItem
import com.example.do_music.model.Instrument
import com.example.do_music.model.TheoryInfo
import com.example.do_music.model.Vocal
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface OpenMainApiService {

    @GET("/api/vocals/{vocalsId}")
    suspend fun getVocalById(@Path("vocalsId") vocalsId: Int): Vocal

    @GET("/api/vocals/list")
    suspend fun getVocals(
        @Query("searchText") searchText : String,
        @Query("pageNumber") pageNumber : Int,
        @Query("pageSize") pageSize: Int = 10
    ): GetVocalsResponse


    @GET("api/notes/musician")
    suspend fun getNotesByCompositor(
        @Query("compositorId") compositorId : Int,
        @Query("noteGroupType") noteGroupType : String,
        @Query("searchText") searchText : String,
        @Query("pageNumber") pageNumber : Int,
        @Query("pageSize") pageSize: Int = 10
    ): GetInstrumentsResponse


    @GET("/api/instruments/by-name/{instrumentGroupName}")
    suspend fun getInstrumentsByGroupName(@Path("instrumentGroupName") instrumentGroupName: String): List<InstrumentByGroup>

    @GET("api/books/{bookId}")
    suspend fun getBookById(@Path("bookId") bookId: Int): TheoryInfo

    @GET("api/notes/{notesId}")
    suspend fun getInstrumentById(@Path("notesId") notesId: Int): Instrument

    @GET("api/favorites/list")
    suspend fun getFavouriteItems(
        @Query("pageNumber") pageNumber: Int,
        @Query("docType") docType: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("favoriteClass") favoriteClass: String = "",
        @Query("searchText") searchText:String = ""
    ): GetFavouritesResponse


    @GET("api/notes/instruments")
    suspend fun getInstruments(
        @Query("instrument") instrument: Int? = null,
        @Query("pageNumber") pageNumber: Int,
        @Query("instrumentType") instrumentType: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("noteGroupType") noteGroupType: String,
        @Query("searchText") searchText: String = " "
    ): GetInstrumentsResponse

//    @Streaming
//    @GET("api/doc")
//    suspend fun downloadFile(@Query("uniqueName") uniqueName: String): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/favorites/add")
    suspend fun addtoFavourite(
        @Body id: String
    ): FavouriteItem

    @Headers("Content-Type: application/json")
    @POST("api/favorites/class")
    suspend fun addFavouriteClass(
        @Body body: String
    ): FavouriteItem


    @DELETE("api/favorites/remove/{favoriteId}")
    suspend fun removeFromFavourites(@Path("favoriteId") favoriteId: Int): String

    @GET("api/books/list/books")
    suspend fun getBooks(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10,
        @Query("bookType") bookType: String = "",
        @Query("searchText") searchText: String = ""
    ): GetBooksResponse

    @GET("api/compositors")
    suspend fun getCompositors(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10,
        @Query("epoch") epoch: String = "",
        @Query("searchText") searchText: String = ""
    ): GetCompositorsResponse

    @GET("api/compositors/list")
    suspend fun getCompositorsByCountry(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10,
        @Query("country") country: String = "",
        @Query("searchText") searchText: String = ""
    ): GetCompositorsResponse

}


suspend fun OpenMainApiService.returnOrderedCompositorQuery(
    @Query("pageNumber") pageNumber: Int = 0,
    @Query("country") country: String = "",
    @Query("searchText") searchText: String = ""
): GetCompositorsResponse {

    when {
        country != "" -> {
            return getCompositorsByCountry(
                country = country,
                searchText = searchText,
                pageNumber = pageNumber
            )
        }
        else -> {
            return getCompositors(
                searchText = searchText,
                pageNumber = pageNumber
            )

        }
    }
}
