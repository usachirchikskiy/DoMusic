package com.example.do_music.presentation.main
import okhttp3.ResponseBody

data class MainActivityState(
    val nameOfFile: String = "",
    val responseBody: ResponseBody? = null,
    val error: Throwable? = null
)

data class NotificationState(
    val begin: Boolean = false,
    val onComplete: Boolean = false
)

data class UpdateState(
    var favourite:Boolean = false,
    var vocals:Boolean = false,
    var theory:Boolean = false,
    var instruments:Boolean = false
)