package com.sili.do_music.business.datasources.network.main.account


data class UserAccount(
    val avatarId: String?=null,
    val cityId: Int?=null,
    val email: String?=null,
    val fio: String?=null,
    val id: Int?=null,
    val phone: String?=null,
    val regionId: String?=null,
    val roleNames: List<String>?=null,
    val schoolId: Int?=null,
    val username: String?=null
)
