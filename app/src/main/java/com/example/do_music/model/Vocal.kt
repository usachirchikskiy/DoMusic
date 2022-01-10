package com.example.do_music.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "vocals")
data class Vocal(
    val clavierFileName: String,
    val clavierId: String,
    val compositorId: Int,
    val compositorName: String,
    val logoId: String,
    val noteName: String,
    val opusEdition: String,
    @PrimaryKey(autoGenerate = false)
    val vocalsId: Int,
    val favorite:Boolean?= null,
    val favoriteId:Int?=null
)
