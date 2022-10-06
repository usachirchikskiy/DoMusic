package com.sili.do_music.presentation.main
data class MainActivityState(
    val error: Throwable? = null,
    val fileName:String = "",
    val internet:Boolean = false
)

data class NotificationState(
    val begin: Boolean = false,
    val onComplete: Boolean = false
)