package com.sili.do_music.business.datasources.network.auth

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface OpenAuthApiService {
    @Headers("Content-Type: application/json")
    @POST("api/user/login-restore")
    suspend fun loginRestore(@Body params:String): Boolean

    @Headers("Content-Type: application/json")
    @POST("api/user/authenticate")
    suspend fun login(@Body params:String): String

    @POST("api/user/logout")
    suspend fun logout()
}