package com.example.do_music.business.model.main

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_account")
data class UserAccount(
    val avatarId: String?=null,
    val cityId: Int?=null,
    val email: String?=null,
    val fio: String?=null,
    @PrimaryKey(autoGenerate = false)
    val id: Int?=null,
    val phone: String?=null,
    val regionId: String?=null,
    val roleNames: List<String>?=null,
    val schoolId: Int?=null,
    val username: String?=null
)
