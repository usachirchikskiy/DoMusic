package com.sili.do_music.business.model.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instruments")
data class Instrument(
    val bookType: String?=null,
    val clavierFileName: String?=null,
    val clavierId: String?=null,
    val compositorId: Int?=null,
    val compositorName: String?=null,
    val concertsAndFantasies: String?=null,
    val docType: String?=null,
    val ensembles: String?=null,
    val epoch: String?=null,
    var favorite: Boolean,
    val favoriteId: Int?=null,
    val instrumentGroupId: Int?=null,
    val instrumentGroupName: String?=null,
    val instrumentGroupNameRu: String?=null,
    val instrumentId: Int,
    val instrumentName: String?=null,
    val introductionsAndVariations: String?=null,
    val logoId: String?=null,
    @PrimaryKey(autoGenerate = false)
    val noteId: Int,
    val noteName: String?=null,
    val opusEdition: String?=null,
    val partFileName: String?=null,
    val partId: String?=null,
    val playsAndSolos: String?=null,
    val sonatas: String?=null,
    val studiesAndExercises: String?=null
)