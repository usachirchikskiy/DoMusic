package com.example.do_music.interactors.auth

import android.util.Log
import com.example.do_music.network.auth.OpenAuthApiService
import com.example.do_music.util.Constants.Companion.LOGIN
import com.example.do_music.util.Constants.Companion.PASSWORD
import com.example.do_music.util.Constants.Companion.SUCCESS
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

private const val TAG = "CheckUserAuth"

class CheckUserAuth (
    private val api: OpenAuthApiService
) {
    fun logout(): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        try {
            api.logout()
            emit(Resource.success(SUCCESS))
        } catch (throwable: Exception) {
            Log.d(TAG, "logout: " + throwable.message)
            emit(
                Resource.error<String>(throwable)
            )
        }

    }

    fun execute(
        login: String,
        password: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val jsonObject = JSONObject()
        jsonObject.put(LOGIN, login)
        jsonObject.put(PASSWORD, password)
        try {
            val response = api.login(jsonObject.toString())
            Log.d(TAG, "execute: $response")
            if (response == SUCCESS) {
                emit(Resource.success(SUCCESS))
            }
        } catch (throwable: Exception) {
            Log.d(TAG, "execute: " + throwable.message)
            emit(
                Resource.error<String>(throwable)
            )
        }
    }
}

