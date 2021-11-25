package com.example.do_music.di.main


import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.data.home.favourites.FavouritesDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.network.main.BasicAuthInterceptor
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.interactors.SearchCompositors
import com.example.do_music.interactors.SearchInstruments
import com.example.do_music.interactors.SearchTheory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor):OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(basicAuthInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideOpenMainApiService(retrofitBuilder: Retrofit.Builder,okHttpClient: OkHttpClient): OpenMainApiService {
        return retrofitBuilder.
             client(okHttpClient)
            .build()
            .create(OpenMainApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchCompositors(compositorsDao: CompositorsDao, openMainApiService: OpenMainApiService): SearchCompositors {
        return SearchCompositors(openMainApiService,compositorsDao)
    }

    @Singleton
    @Provides
    fun provideSearchTheory(theoryDao: TheoryDao, openMainApiService: OpenMainApiService): SearchTheory {
        return SearchTheory(openMainApiService,theoryDao)
    }

    @Singleton
    @Provides
    fun provideAddToFavourite(openMainApiService: OpenMainApiService,favouritesDao: FavouritesDao): AddToFavourite {
        return AddToFavourite(openMainApiService,favouritesDao)
    }
    @Singleton
    @Provides
    fun provideSearchInstruments(instrumentsDao: InstrumentsDao, openMainApiService: OpenMainApiService): SearchInstruments {
        return SearchInstruments(openMainApiService,instrumentsDao)
    }
}