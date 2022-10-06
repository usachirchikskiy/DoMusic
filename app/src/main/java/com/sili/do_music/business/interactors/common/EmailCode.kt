package com.sili.do_music.business.interactors.common

import android.util.Log
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.datasources.network.main.account.UserChangeResponse
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

private const val TAG = "EmailCode"

class EmailCode (
    private val api: OpenMainApiService,
) {

    fun execute(email: String): Flow<Resource<UserChangeResponse>> = flow {
        emit(Resource.loading())
        val response = api.prepareNewEmail(email)
        emit(Resource.success(response))
    }.catch { e ->
        Log.d(TAG, "execute: " + e.toString())
        emit(Resource.error<UserChangeResponse>(e))
    }

    fun confirmEmail(code: String): Flow<Resource<UserChangeResponse>> = flow {
        emit(Resource.loading())
        val response = api.emailConfirm(code)
        emit(Resource.success(response))
    }.catch { e ->
        Log.d(TAG, "execute: " + e.toString())
        emit(Resource.error<UserChangeResponse>(e))
    }

}