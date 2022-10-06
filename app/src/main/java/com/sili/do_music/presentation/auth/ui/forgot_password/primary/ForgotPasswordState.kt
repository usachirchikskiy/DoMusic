package com.sili.do_music.presentation.auth.ui.forgot_password.primary

data class ForgotPasswordState(
    val onComplete:Boolean = false,
    val error: Throwable?=null
)
