package com.sili.do_music.business.datasources.network.main.account

data class UserChangeResponse(
    val avatarId: String?=null,
    val changePass: Int?=null,
    val cityId: Int?=null,
    val email: String?=null,
    val enabled: Boolean?=null,
    val fio: String?=null,
    val id: Int?=null,
    val login: String?=null,
    val matchingPassword: String?=null,
    val password: String?=null,
    val phone: String?=null,
    val regionId: String?=null,
    val role: String?=null
)