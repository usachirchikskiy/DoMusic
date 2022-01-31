package com.example.do_music.network.main

import com.example.do_music.model.FavouriteItem
import com.example.do_music.model.UserAccount
import com.example.do_music.network.main.account.UserChangeResponse
import com.example.do_music.network.main.account.UserPhotoResponse
import com.example.do_music.network.main.favourite.GetFavouritesResponse
import com.example.do_music.network.main.home.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface OpenMainApiService {

    @Multipart
    @POST("api/doc/avatar")
    suspend fun uploadPhotoToServer(
        @Part image: MultipartBody.Part?
    ): UserPhotoResponse

    @Multipart
    @POST("api/user/feedback")
    suspend fun uploadFilesToServer(
        @Query("comment") comment: String,
        @Part files: List<MultipartBody.Part?>
    ): Response<String>

    @POST("api/user/phone/{phone}")
    suspend fun userPhone(@Path("phone") phone: String): UserChangeResponse

    @POST("api/user/password/confirm/{otpCode}/{newPassword}/{repeatedNewPassword}")
    suspend fun passwordConfirm(
        @Path("otpCode") otpCode: String,
        @Path("newPassword") newPassword: String,
        @Path("repeatedNewPassword") repeatedNewPassword: String,
    ): UserChangeResponse

    @POST("api/user/password/check/{otpCode}")
    suspend fun passwordCheck(@Path("otpCode") otpCode: String): UserChangeResponse

    @POST("api/user/password/prepare")
    suspend fun passwordPrepare(): UserChangeResponse

    @POST("api/user/email/confirm/{code}")
    suspend fun emailConfirm(@Path("code") code: String): UserChangeResponse

    @POST("api/user/email/prepare/{newEmail}")
    suspend fun prepareNewEmail(@Path("newEmail") newEmail: String): UserChangeResponse

    @GET("/api/user")
    suspend fun getUserAccount(): UserAccount

//    @GET("api/books/{bookId}")
//    suspend fun getBookById(@Path("bookId") bookId: Int): TheoryInfo
//
//    @GET("api/notes/{notesId}")
//    suspend fun getInstrumentById(@Path("notesId") notesId: Int): Instrument
//
//    @GET("/api/vocals/{vocalsId}")
//    suspend fun getVocalById(@Path("vocalsId") vocalsId: Int): Vocal

    @Headers("Content-Type: application/json")
    @POST("api/favorites/class")
    suspend fun addFavouriteClass(
        @Body body: String
    ): FavouriteItem

    @DELETE("api/favorites/remove/{favoriteId}")
    suspend fun removeFromFavourites(@Path("favoriteId") favoriteId: Int): String

    @GET("api/favorites/list")
    suspend fun getFavouriteItems(
        @Query("pageNumber") pageNumber: Int,
        @Query("docType") docType: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("favoriteClass") favoriteClass: String = "",
        @Query("searchText") searchText: String = ""
    ): GetFavouritesResponse

    @Headers("Content-Type: application/json")
    @POST("api/favorites/add")
    suspend fun addtoFavourite(
        @Body id: String
    ): FavouriteItem

    @GET("/api/vocals/list")
    suspend fun getVocals(
        @Query("searchText") searchText: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int = 10
    ): GetVocalsResponse

    @GET("/api/instruments/by-name/{instrumentGroupName}")
    suspend fun getInstrumentsByGroupName(@Path("instrumentGroupName") instrumentGroupName: String): List<InstrumentByGroup>

    @GET("api/notes/instruments")
    suspend fun getInstruments(
        @Query("instrument") instrument: Int? = null,
        @Query("pageNumber") pageNumber: Int,
        @Query("instrumentType") instrumentType: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("noteGroupType") noteGroupType: String,
        @Query("searchText") searchText: String = " "
    ): GetInstrumentsResponse

    @GET("api/books/list/books")
    suspend fun getBooks(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10,
        @Query("bookType") bookType: String = "",
        @Query("searchText") searchText: String = ""
    ): GetBooksResponse

    @GET("api/compositors/list")
    suspend fun getCompositors(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10,
        @Query("country") country: String = "",
        @Query("searchText") searchText: String = ""
    ): GetCompositorsResponse


    @GET("api/vocals/musician")
    suspend fun getVocalNotesByCompositor(
        @Query("compositorId") compositorId: Int,
        @Query("searchText") searchText: String = "",
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int = 10
    ): GetVocalsResponse


    @GET("api/notes/musician")
    suspend fun getInstrumentalNotesByCompositor(
        @Query("compositorId") compositorId: Int,
        @Query("noteGroupType") noteGroupType: String = "",
        @Query("searchText") searchText: String = "",
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int = 10
    ): GetInstrumentsResponse

}
