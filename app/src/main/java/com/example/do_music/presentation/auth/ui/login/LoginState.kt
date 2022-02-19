package com.example.do_music.presentation.auth.ui.login

data class LoginState(
    val isLoading: Boolean = false,
    val error: Throwable?=null
)
