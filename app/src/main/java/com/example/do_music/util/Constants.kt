package com.example.do_music.util
import com.example.do_music.ui.main.home.adapter.InstrumentHelper

class Constants {

    companion object {
        const val PASSWORD = "password"
        const val LOGIN = "login"
        const val SUCCESS = "Success"
        const val SHOULD_REFRESH = "should_refresh"
        const val PAGINATION_PAGE_SIZE = 10
        const val BASE_URL = "https://domusic.uz/"
        const val VOCAL_GROUP = "vocal"
        const val INSTRUMENTAL_GROUP = "instrumental"
        const val NOTE_ID = "noteId"
        const val VOCALS_ID = "vocalsId"
        const val BOOK_ID = "bookId"
        const val ITEM_ID = "itemId"
        const val COMPOSITOR_ID = "compositorId"
        const val NAME_OF_COMPOSITOR = "nameOfCompositor"
        const val FRAGMENT = "fragment"
        const val BOOK = "BOOK"
        const val VOCALS = "VOCALS"
        const val NOTES = "NOTES"
        const val FILTER_UZB = "UZB"
        const val FILTER_RUSSIAN = "RUSSIAN"
        const val FILTER_FOREIGN = "FOREIGN"
        const val SUCCESS_CODE = 200
        const val CODE = "optCode"
        const val AUTH_ERROR = "HTTP 401"
        const val DOWNLOAD_FILE_PART_LINK = "api/doc?uniqueName="
        const val AUTHORIZATION = "Authorization"
        const val NO_INTERNET =  "failed to connect to XXXX"

        val FILTERS = arrayListOf(
            InstrumentHelper(GroupName = "WOODWIND", name = "Деревянные духовые"),
            InstrumentHelper(GroupName = "BRASS", name = "Медные духовые"),
            InstrumentHelper(GroupName = "STRINGED_BOWS", name = "Струнно-смычковые"),
            InstrumentHelper(GroupName = "PIANOS", name = "Фортепиано"),
            InstrumentHelper(GroupName = "UZBEK_FOLK", name = "Узбекские народные")
        )
        val FILTERS_ENSAMBLE = arrayListOf(
            InstrumentHelper(Ansamble = "ENSEMBLES", name = "Ансамбли"),
            InstrumentHelper(Ansamble = "INTRODUCTIONS_AND_VARIATIONS", name = "Рондо"),
            InstrumentHelper(Ansamble = "CONCERTS_AND_FANTASIES", name = "Концерты и соло"),
            InstrumentHelper(Ansamble = "PLAYS_AND_SOLOS", name = "Пьесы и фантазии"),
            InstrumentHelper(Ansamble = "SONATAS", name = "Сонаты"),
            InstrumentHelper(Ansamble = "STUDIES_AND_EXERCISES", name = "Этюды и упражнения")
        )


    }
}