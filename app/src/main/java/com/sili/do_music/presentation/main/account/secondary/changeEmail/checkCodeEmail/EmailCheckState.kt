package com.sili.do_music.presentation.main.account.secondary.changeEmail.checkCodeEmail

data class EmailCheckState(
    val isLoading:Boolean = false,
    val onComplete:Boolean = false,
    val error:Throwable ?= null,
)
