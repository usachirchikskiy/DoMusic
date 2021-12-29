package com.example.do_music.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "theory_and_literature")
data class TheoryInfo(
    val authorId: Int,
    val authorName: String,
    val bookFileId: String,
    val bookFileName: String,
    @PrimaryKey(autoGenerate = false)
    val bookId: Int,
    val bookName: String,
    val bookType: String,
    val logoId: String,
    val opusEdition: String,
    val favorite:Boolean,
    val favoriteId:Int?=null
)
