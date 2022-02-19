package com.example.do_music.di.auth

import com.example.do_music.business.datasources.data.account.UserAccountDao
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.datasources.data.home.compositors.CompositorsDao
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.datasources.network.auth.OpenAuthApiService
import com.example.do_music.business.interactors.auth.CheckUserAuth
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
        openAuthApiService: OpenAuthApiService,
        userAccountDao: UserAccountDao,
        favouritesDao: FavouritesDao,
        compositorsDao: CompositorsDao,
        instrumentsDao: InstrumentsDao,
        theoryDao: TheoryDao,
        vocalsDao: VocalsDao,
    ): CheckUserAuth {
        return CheckUserAuth(
            openAuthApiService,
            userAccountDao,
            favouritesDao,
            compositorsDao,
            instrumentsDao,
            theoryDao,
            vocalsDao
        )
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