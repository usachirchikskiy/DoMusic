package com.sili.do_music.presentation.main.account.primary

import android.net.Uri
import com.sili.do_music.business.model.main.TeacherAccount

data class UserAccountState(
    val userAccount: TeacherAccount? =null,
    val uri: Uri? = null,
    val error: Throwable?=null,
    val filesPath:ArrayList<String> = arrayListOf(),
    val data:String = "",
    val completed:Boolean = false
    )