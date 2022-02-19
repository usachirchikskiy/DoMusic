package com.example.do_music.presentation.main

import okhttp3.ResponseBody


data class MainActivityState(
    val progress: Int = 0,
    val nameOfFile: String = "",
    val responseBody: ResponseBody? = null,
    val error: Throwable? = null
)