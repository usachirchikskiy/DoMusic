package com.example.do_music.presentation.common.itemSelected

import com.example.do_music.business.model.main.Instrument
import com.example.do_music.business.model.main.TheoryInfo
import com.example.do_music.business.model.main.Vocal

data class ItemState(
    val error: Throwable?= null,
    val vocal: Vocal?=null,
    val instrument: Instrument?=null,
    val book: TheoryInfo?=null
)