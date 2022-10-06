package com.sili.do_music.business.datasources.network.main.account

data class UserPhotoResponse(
    val code: Int?=null,
    val errors: List<String>?=null,
    val message: String?=null,
    val result: AvatarResultField?=null,
    val status: String?=null
)