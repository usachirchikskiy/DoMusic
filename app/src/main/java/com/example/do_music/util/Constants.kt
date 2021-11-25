package com.example.do_music.util

import com.example.do_music.main.ui.home.adapter.InstrumentHelper

class Constants {

    companion object {
        const val PAGINATION_PAGE_SIZE: Int = 10
        const val PREF_NAME: String = "LoginSession"
        const val BASE_URL = "https://domusic.uz/"
        val FILTERS = arrayListOf(
            InstrumentHelper(GroupName = "WOODWIND", name = "Деревянные духовые"),
            InstrumentHelper(GroupName = "BRASS", name = "Медные духовые"),
            InstrumentHelper(GroupName = "STRINGED_BOWS", name = "Струнно-смычковые"),
            InstrumentHelper(GroupName = "PIANOS", name = "Фортепиано"),
            InstrumentHelper(GroupName = "UZBEK_FOLK", name = "Узбекские народные")
        )
        val FILTERSANSAMBLE = arrayListOf(
            InstrumentHelper(Ansamble = "ENSEMBLES", name = "Ансамбли"),
            InstrumentHelper(Ansamble = "INTRODUCTIONS_AND_VARIATIONS", name = "Рондо"),
            InstrumentHelper(Ansamble = "CONCERTS_AND_FANTASIES", name = "Концерты и соло"),
            InstrumentHelper(Ansamble = "PLAYS_AND_SOLOS", name = "Пьесы и фантазии"),
            InstrumentHelper(Ansamble = "SONATAS", name = "Сонаты"),
            InstrumentHelper(Ansamble = "STUDIES_AND_EXERCISES", name = "Этюды и упражнения")
        )

    }
}