package com.sili.do_music.business.model.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class Favourite(
    val author: Boolean?=null,
    val bookClass: String?=null,
    val bookFileId: String?=null,
    val bookId: Int?=null,
    val bookLogoId: String?=null,
    val bookName: String?=null,
    val bookType: String?=null,
    val compositorId: Int?=null,
    val compositorName: String?=null,
    @PrimaryKey(autoGenerate = false)
    val favoriteId: Int,
    val fileClavierId: String?=null,
    val filePartId: String?=null,
    val instrumentId: Int?=null,
    val instrumentName: String?=null,
    val logoId: String?=null,
    val musician: Boolean?=null,
    val noteId: Int?=null,
    val noteName: String?=null,
    val notesClass: String?=null,
    val opusEdition: String?=null,
    val userId: Int?=null,
    val vocalsClass: String?=null,
    val vocalsId: Int?=null
)