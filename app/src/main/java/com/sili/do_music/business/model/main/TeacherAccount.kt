package com.sili.do_music.business.model.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class TeacherAccount(
    val avatarId: String? = null,
    val changePass: Boolean? = null,
    val cityId: Int? = null,
    val cityName: String? = null,
    val email: String? = null,
    val fio: String? = null,
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val password: String? = null,
    val phone: String? = null,
    val rePassword: String? = null,
    val regionId: String? = null,
    val regionName: String? = null,
    val roleNames: List<String>? = null,
    val schoolId: Int? = null,
    val schoolName: String? = null,
    val username: String? = null
)