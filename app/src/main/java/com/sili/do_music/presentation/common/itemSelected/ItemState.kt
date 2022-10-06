package com.sili.do_music.presentation.common.itemSelected

import com.sili.do_music.business.model.main.Instrument
import com.sili.do_music.business.model.main.TheoryInfo
import com.sili.do_music.business.model.main.Vocal

data class ItemState(
    val vocal: Vocal?=null,
    val instrument: Instrument?=null,
    val book: TheoryInfo?=null
)