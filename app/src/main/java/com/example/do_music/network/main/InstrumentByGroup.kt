package com.example.do_music.network.main

data class InstrumentByGroup(
    val fileId: String,
    val id: Int,
    val instrumentGroupId: Int,
    val nameRu: String,
    val nameUz: String,
    val nameUzCyrl: String
)