package com.example.do_music.model

import androidx.room.PrimaryKey

data class CompositorInfo(
//    val pk: Int,
    val musician: Boolean,
    val author: Boolean,
    val instime: String,
    val updtime: String?=null,
    val epoch:String,
    val id: Int,
    val fileId:String,
    val name:String,
    val country: String
)
