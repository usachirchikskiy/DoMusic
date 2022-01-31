package com.example.do_music.di.main


import com.example.do_music.data.account.UserAccountDao
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.data.favourites.FavouritesDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.data.home.vocal.VocalsDao
import com.example.do_music.interactors.account.GetUserAccount
import com.example.do_music.interactors.common.EmailCode
import com.example.do_music.interactors.common.PasswordCode
import com.example.do_music.interactors.favourite.AddToFavourite
import com.example.do_music.interactors.home.*
import com.example.do_music.network.main.OpenMainApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideSearchCompositors(
        compositorsDao: CompositorsDao,
        openMainApiService: OpenMainApiService
    ): SearchCompositors {
        return SearchCompositors(openMainApiService, compositorsDao)
    }

    @Singleton
    @Provides
    fun provideSearchVocals(
        vocalsDao: VocalsDao,
        openMainApiService: OpenMainApiService
    ): SearchVocals {
        return SearchVocals(openMainApiService, vocalsDao)
    }

    @Singleton
    @Provides
    fun provideSearchTheory(
        theoryDao: TheoryDao,
        openMainApiService: OpenMainApiService
    ): SearchTheory {
        return SearchTheory(openMainApiService, theoryDao)
    }

    @Singleton
    @Provides
    fun provideAddToFavourite(
        openMainApiService: OpenMainApiService,
        instrumentsDao: InstrumentsDao,
        vocalsDao: VocalsDao,
        theoryDao: TheoryDao,
        favouritesDao: FavouritesDao
    ): AddToFavourite {
        return AddToFavourite(
            openMainApiService,
            instrumentsDao,
            vocalsDao,
            theoryDao,
            favouritesDao
        )
    }

    @Singleton
    @Provides
    fun provideSearchInstruments(
        instrumentsDao: InstrumentsDao,
        openMainApiService: OpenMainApiService
    ): SearchInstruments {
        return SearchInstruments(openMainApiService, instrumentsDao)
    }

    @Singleton
    @Provides
    fun provideSearchFavourites(
        openMainApiService: OpenMainApiService,
        favouritesDao: FavouritesDao
    ): SearchFavourites {
        return SearchFavourites(openMainApiService, favouritesDao)
    }

    @Singleton
    @Provides
    fun provideSearchItem(
        vocalsDao: VocalsDao,
        instrumentsDao: InstrumentsDao,
        theoryDao: TheoryDao
    ): SearchItem {
        return SearchItem(vocalsDao, instrumentsDao, theoryDao)
    }

    @Singleton
    @Provides
    fun provideGetUserAccount(
        openMainApiService: OpenMainApiService,
        userAccountDao: UserAccountDao
    ): GetUserAccount {
        return GetUserAccount(userAccountDao, openMainApiService)
    }

    @Singleton
    @Provides
    fun provideSearchCompositorSelected(
        openMainApiService: OpenMainApiService,
        vocalsDao: VocalsDao,
        instrumentsDao: InstrumentsDao
    ): SearchCompositorSelected {
        return SearchCompositorSelected(openMainApiService, instrumentsDao, vocalsDao)
    }

    @Singleton
    @Provides
    fun providePasswordCode(
        openMainApiService: OpenMainApiService,
    ): PasswordCode {
        return PasswordCode(openMainApiService)
    }

    @Singleton
    @Provides
    fun provideEmailCode(
        openMainApiService: OpenMainApiService
    ): EmailCode {
        return EmailCode(openMainApiService)
    }

}