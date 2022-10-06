package com.sili.do_music.presentation.main.account.secondary.changePassword.checkCode

data class PasswordCheckState(
    val isLoading:Boolean = false,
    val onComplete:Boolean = false,
    val error:Throwable ?= null,
    val code:String = ""
)
