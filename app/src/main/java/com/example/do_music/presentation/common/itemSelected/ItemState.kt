package com.example.do_music.presentation.common.itemSelected

import com.example.do_music.business.model.main.Instrument
import com.example.do_music.business.model.main.TheoryInfo
import com.example.do_music.business.model.main.Vocal
import okhttp3.ResponseBody

data class ItemState(
    val vocal: Vocal?=null,
    val instrument: Instrument?=null,
    val book: TheoryInfo?=null
)