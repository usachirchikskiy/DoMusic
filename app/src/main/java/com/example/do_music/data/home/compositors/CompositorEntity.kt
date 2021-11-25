package com.example.do_music.data.home.compositors

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.do_music.model.CompositorInfo
import com.google.gson.annotations.SerializedName

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

fun CompositorEntity.toCompositorInfo(): CompositorInfo {
    return CompositorInfo(
//        pk=pk,
        musician = musician,
        author = author,
        instime = instime,
        updtime = updtime,
        epoch = epoch,
        id = id,
        fileId = fileId,
        name = name,
        country = country
    )
}


fun CompositorInfo.toEntity(): CompositorEntity {
    return CompositorEntity(
//        pk = pk,
        musician = musician,
        author = author,
        instime = instime,
        updtime = updtime,
        epoch = epoch,
        id = id,
        fileId = fileId,
        name = name,
        country = country
    )
}




