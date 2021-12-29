package com.example.do_music.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.do_music.model.CompositorEntity
import com.example.do_music.data.home.compositors.CompositorsDao
import com.example.do_music.data.home.favourites.FavouritesDao
import com.example.do_music.data.home.instruments.InstrumentsDao
import com.example.do_music.data.home.theory.TheoryDao
import com.example.do_music.data.home.vocal.VocalsDao
import com.example.do_music.model.Favourite
import com.example.do_music.model.Instrument
import com.example.do_music.model.TheoryInfo
import com.example.do_music.model.Vocal


@Database(
    entities =
    [CompositorEntity::class,
        TheoryInfo::class,
        Instrument::class,
        Favourite::class,
        Vocal::class],
    version = 1
)
abstract class DoMusicDatabase : RoomDatabase() {

    abstract fun instrumentsDao(): InstrumentsDao

    abstract fun theoryDao(): TheoryDao

    abstract fun compositorsDao(): CompositorsDao

    abstract fun favouritesDao(): FavouritesDao

    abstract fun vocalsDao(): VocalsDao
}
