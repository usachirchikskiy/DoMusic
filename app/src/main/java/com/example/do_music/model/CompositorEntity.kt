package com.example.do_music.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compositors")
data class CompositorEntity(
    val musician: Boolean,
    val author: Boolean,
    val instime: String,
    val updtime: String? = null,
    val epoch: String,
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val fileId: String,
    val name: String,
    val country: String
)





