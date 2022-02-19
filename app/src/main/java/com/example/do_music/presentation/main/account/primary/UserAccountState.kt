package com.example.do_music.presentation.main.account.primary

import android.net.Uri
import com.example.do_music.business.model.main.UserAccount

data class UserAccountState(
    val userAccount: UserAccount? =null,
    val uri: Uri? = null,
    val error: Throwable?=null,
    val filesPath:ArrayList<String> = arrayListOf(),
    val data:String = "",
    val completed:Boolean = false
    )