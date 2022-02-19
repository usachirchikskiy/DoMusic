package com.example.do_music.business.interactors.common

import android.util.Log
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.business.datasources.network.main.account.UserChangeResponse
import com.example.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

private const val TAG = "PasswordCode"

class PasswordCode (
    private val api: OpenMainApiService,
) {
    fun execute(passwordCode: String): Flow<Resource<UserChangeResponse>> = flow {
        emit(Resource.loading())
        val response = api.passwordCheck(passwordCode)
        emit(Resource.success(response))
    }.catch { e ->
        emit(Resource.error<UserChangeResponse>(e))
    }

    fun confirmPassword(optCode:String,newPassword:String,repeatedNewPassword: String): Flow<Resource<UserChangeResponse>> = flow {
        emit(Resource.loading())
        val response = api.passwordConfirm(optCode,newPassword,repeatedNewPassword)
        emit(Resource.success(response))
    }.catch { e ->
        Log.d(TAG, "execute: " + e.toString())
        emit(Resource.error<UserChangeResponse>(e))
    }
}
