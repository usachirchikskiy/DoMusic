package com.example.do_music.network.auth

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface OpenAuthApiService {


    @Headers("Content-Type: application/json")
    @POST("api/user/authenticate")
    fun login(@Body params:String): Call<ResponseBody>
}