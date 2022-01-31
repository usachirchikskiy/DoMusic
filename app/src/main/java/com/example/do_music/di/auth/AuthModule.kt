package com.example.do_music.di.auth

import com.example.do_music.interactors.auth.CheckUserAuth
import com.example.do_music.network.auth.OpenAuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideCheckUserAuth(
        openAuthApiService: OpenAuthApiService
    ): CheckUserAuth {
        return CheckUserAuth(openAuthApiService)
    }

    @Singleton
    @Provides
    fun provideOpenApiAuthService(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): OpenAuthApiService {
        return retrofitBuilder.client(okHttpClient)
            .build()
            .create(OpenAuthApiService::class.java)
    }
}