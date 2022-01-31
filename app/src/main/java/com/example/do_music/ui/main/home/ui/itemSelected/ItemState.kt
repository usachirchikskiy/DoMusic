package com.example.do_music.ui.main.home.ui.itemSelected

import com.example.do_music.model.Instrument
import com.example.do_music.model.TheoryInfo
import com.example.do_music.model.Vocal

data class ItemState(
    val error: Throwable?= null,
    val vocal: Vocal?=null,
    val instrument: Instrument?=null,
    val book: TheoryInfo?=null
)