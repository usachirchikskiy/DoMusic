package com.example.do_music.di

import android.app.Application
import androidx.room.Room
import com.example.do_music.data.DoMusicDatabase
import com.example.do_music.data.account.UserAccountDao
import com.example.do_music.data.favourites.FavouritesDao
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.data.home.vocal.VocalsDao
import com.example.do_music.datastore.AppDataStore
import com.example.do_music.datastore.AppDataStoreManager
import com.example.do_music.interactors.BasicAuthInterceptor
import com.example.do_music.network.main.OpenMainApiService
import com.example.do_music.session.SessionManager
import com.example.do_music.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Singleton
    @Provides
    fun provideDataStoreManager(
        application: Application
    ): AppDataStore {
        return AppDataStoreManager(application)
    }

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gsonBuilder: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
    }

    @Singleton
    @Provides
    fun provideBasicAuthInterceptor(sessionManager: SessionManager): BasicAuthInterceptor {
        return BasicAuthInterceptor(sessionManager)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(basicAuthInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideOpenMainApiService(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): OpenMainApiService {

        return retrofitBuilder.client(okHttpClient)
            .build()
            .create(OpenMainApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(application: Application): DoMusicDatabase =
        Room.databaseBuilder(application, DoMusicDatabase::class.java, "do_music_database")
            .build()

    @Singleton
    @Provides
    fun provideCompositorsDao(db: DoMusicDatabase): CompositorsDao {
        return db.compositorsDao()
    }


    @Singleton
    @Provides
    fun provideTheoryDao(db: DoMusicDatabase): TheoryDao {
        return db.theoryDao()
    }

    @Singleton
    @Provides
    fun provideFavouritesDao(db: DoMusicDatabase): FavouritesDao {
        return db.favouritesDao()
    }

    @Singleton
    @Provides
    fun provideInstrumentsDao(db: DoMusicDatabase): InstrumentsDao {
        return db.instrumentsDao()
    }


    @Singleton
    @Provides
    fun provideVocalsDao(db: DoMusicDatabase): VocalsDao {
        return db.vocalsDao()
    }

    @Singleton
    @Provides
    fun provideUserAccountDao(db: DoMusicDatabase): UserAccountDao {
        return db.userAccountDao()
    }

}