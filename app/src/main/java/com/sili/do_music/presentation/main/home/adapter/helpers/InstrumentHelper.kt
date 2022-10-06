package com.sili.do_music.presentation.main.home.adapter.helpers

data class InstrumentHelper(
    var isGroupName: Boolean = false,
    var isAnsamble: Boolean = false,
    var isInstumentId: Boolean = false,
    var name: String = "",
    var GroupName: String = "",
    var Ansamble: String = "",
    var InstumentId: Int = -1,
)