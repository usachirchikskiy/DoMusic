package com.example.do_music.di.auth

import com.example.do_music.network.auth.OpenAuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenAuthApiService {
        return retrofitBuilder
            .build()
            .create(OpenAuthApiService::class.java)
    }
}