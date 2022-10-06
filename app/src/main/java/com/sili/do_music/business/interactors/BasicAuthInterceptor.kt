package com.sili.do_music.business.interactors

import com.sili.do_music.presentation.session.SessionManager
import com.sili.do_music.util.Constants.Companion.AUTHORIZATION
import okhttp3.Credentials
import okhttp3.Interceptor

private const val TAG = "BasicAuthInterceptor"


class BasicAuthInterceptor (
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var credentials:String
        var request = chain.request()
        if(!sessionManager.state.value?.login.isNullOrBlank()) {
            credentials =
                Credentials.basic(
                    sessionManager.state.value?.login,
                    sessionManager.state.value?.password
                )
            request = request.newBuilder().header(AUTHORIZATION, credentials).build()
        }

//        Log.d(
//            TAG,
//            "\nRequest " + request.url() + "\n" + request.body() + "\ncredentials: " + sessionManager.state.value?.login + "\n" + sessionManager.state.value?.password
//        )

        return chain.proceed(request)
    }
}