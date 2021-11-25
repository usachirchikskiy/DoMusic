package com.example.do_music.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    var isFavourite:Boolean?= false
):Parcelable