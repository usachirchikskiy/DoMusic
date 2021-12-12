package com.example.do_music.network.main

import com.example.do_music.session.SessionManager
import okhttp3.Credentials
import okhttp3.Interceptor
import okio.Buffer
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasicAuthInterceptor  @Inject constructor(
    private val sessionManager: SessionManager
    ): Interceptor
{
    private var credentials: String = Credentials.basic(sessionManager.getlogin(),sessionManager.getpassword())

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(request)
//        return originalResponse
    }
}