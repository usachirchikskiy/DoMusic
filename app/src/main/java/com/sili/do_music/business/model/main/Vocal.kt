package com.sili.do_music.business.model.main

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
    var favorite:Boolean,
    val favoriteId:Int?=null
)
