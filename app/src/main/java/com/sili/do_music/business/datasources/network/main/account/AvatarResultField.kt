package com.sili.do_music.business.datasources.network.main.account

data class AvatarResultField(
    val accountNonExpired: Boolean?=null,
    val accountNonLocked: Boolean?=null,
    val avatarId: String?=null,
    val credentialsNonExpired: Boolean?=null,
    val email: String?=null,
    val fio: String?=null,
    val id: Int?=null,
    val phone: String?=null,
    val roles: List<String>?=null,
    val username: String?=null
)