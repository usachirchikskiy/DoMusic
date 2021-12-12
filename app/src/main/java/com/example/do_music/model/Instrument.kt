package com.example.do_music.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instruments")
data class Instrument(
    val bookType:  String?=null,
    val clavierFileName:  String?=null,
    val clavierId:  String?=null,
    val compositorId: Int?=null,
    val compositorName:  String?=null,
    val concertsAndFantasies:  String?=null,
    val docType:  String?=null,
    val ensembles:  String?=null,
    val epoch:  String?=null,
    val instrumentGroupId: Int?=null,
    val instrumentGroupName:  String?=null,
    val instrumentGroupNameRu:  String?=null,
    val instrumentId: Int?=null,
    val instrumentName:  String?=null,
    val introductionsAndVariations:  String?=null,
    val logoId:  String?=null,
    @PrimaryKey(autoGenerate = false)
    val noteId: Int?=null,
    val noteName:  String?=null,
    val opusEdition:  String?=null,
    val partFileName:  String?=null,
    val partId:  String?=null,
    val playsAndSolos:  String?=null,
    val sonatas:  String?=null,
    val studiesAndExercises:  String?=null,
    var isFavourite:Boolean?= false
)
//"docType": "NOTES",
//"bookType": null,
//"noteId": 2141,
//"logoId": "0c801cbe-e1fb-4136-9190-99ac59ea3a71",
//"clavierId": "27b01663-c90f-4385-96e0-53bb77af2f3c",
//"clavierFileName": "Соната клавир.pdf",
//"partId": "72eba104-ca40-4e99-b80e-ef4f11cf2166",
//"partFileName": "Соната партия.pdf",
//"noteName": "Соната ",
//"opusEdition": "",
//"instrumentGroupId": 1,
//"instrumentGroupName": "WOODWIND",
//"instrumentGroupNameRu": "Деревянные духовые",
//"instrumentId": 3,
//"instrumentName": "Кларнет",
//"compositorId": 1187,
//"compositorName": "Аарон Копленд",
//"epoch": "UNKNOWN",
//"ensembles": "0",
//"introductionsAndVariations": "0",
//"concertsAndFantasies": "0",
//"playsAndSolos": "0",
//"sonatas": "1",
//"studiesAndExercises": "0"