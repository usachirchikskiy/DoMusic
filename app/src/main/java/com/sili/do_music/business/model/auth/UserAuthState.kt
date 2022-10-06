package com.sili.do_music.business.model.auth

data class UserAuthState(
    val login:String?="",
    val password:String?="",
    val error: Throwable? = null,
    val onStarMainActivity: Boolean = false,
    val onStarAuthActivity: Boolean = false
)