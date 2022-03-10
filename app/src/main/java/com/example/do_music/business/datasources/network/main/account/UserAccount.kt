package com.example.do_music.business.datasources.network.main.account

import androidx.room.Entity
import androidx.room.PrimaryKey


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
