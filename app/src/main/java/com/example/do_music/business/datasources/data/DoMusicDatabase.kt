package com.example.do_music.business.datasources.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.do_music.business.datasources.data.account.UserAccountDao
import com.example.do_music.business.datasources.data.home.compositors.CompositorsDao
import com.example.do_music.business.datasources.data.favourites.FavouritesDao
import com.example.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.example.do_music.business.datasources.data.home.theory.TheoryDao
import com.example.do_music.business.datasources.data.home.vocal.VocalsDao
import com.example.do_music.business.model.main.TeacherAccount
import com.example.do_music.business.model.main.*
import com.example.do_music.util.Converters

@TypeConverters(Converters::class)
@Database(
    entities =
    [Compositor::class,
        TheoryInfo::class,
        Instrument::class,
        Favourite::class,
        Vocal::class,
        TeacherAccount::class],
    version = 1
)
abstract class DoMusicDatabase : RoomDatabase() {

    abstract fun instrumentsDao(): InstrumentsDao

    abstract fun theoryDao(): TheoryDao

    abstract fun compositorsDao(): CompositorsDao

    abstract fun favouritesDao(): FavouritesDao

    abstract fun vocalsDao(): VocalsDao

    abstract fun userAccountDao(): UserAccountDao
}
