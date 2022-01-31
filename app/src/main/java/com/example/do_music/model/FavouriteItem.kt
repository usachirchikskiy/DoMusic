package com.example.do_music.model

data class FavouriteItem(
    val bookClass: String,
    val bookId: Int,
    val deleted: Int,
    val id: Int,
    val instime: List<String>,
    val noteId: Int,
    val notesClass: String,
    val updtime: String,
    val userId: Int,
    val vocalsClass: String,
    val vocalsId: Int
)