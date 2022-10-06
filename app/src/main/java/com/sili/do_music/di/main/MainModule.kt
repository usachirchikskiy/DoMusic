package com.sili.do_music.di.main


import android.app.Application
import com.sili.do_music.business.datasources.data.account.UserAccountDao
import com.sili.do_music.business.datasources.data.home.compositors.CompositorsDao
import com.sili.do_music.business.datasources.data.favourites.FavouritesDao
import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.theory.TheoryDao
import com.sili.do_music.business.datasources.data.home.vocal.VocalsDao
import com.sili.do_music.business.interactors.account.GetUserAccount
import com.sili.do_music.business.interactors.home.*
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.interactors.common.*
import com.sili.do_music.business.interactors.favourite.SearchFavourites
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
        openMainApiService: OpenMainApiService,
        compositorsDao: CompositorsDao,
        favouritesDao: FavouritesDao,
        instrumentsDao: InstrumentsDao,
        vocalsDao: VocalsDao,
        theoryDao: TheoryDao
    ): SearchCompositors {
        return SearchCompositors(
            openMainApiService,
            compositorsDao,
            favouritesDao,
            instrumentsDao,
            vocalsDao,
            theoryDao
        )
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
        return SearchFavourites(
            openMainApiService,
            favouritesDao
        )
    }

    @Singleton
    @Provides
    fun provideSearchItem(
        openMainApiService: OpenMainApiService,
        vocalsDao: VocalsDao,
        instrumentsDao: InstrumentsDao,
        theoryDao: TheoryDao
    ): SearchItem {
        return SearchItem(openMainApiService, vocalsDao, instrumentsDao, theoryDao)
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

    @Singleton
    @Provides
    fun provideDownloadFile(
        openMainApiService: OpenMainApiService,
        application: Application
    ): DownloadFile {
        return DownloadFile(openMainApiService,application)
    }

}