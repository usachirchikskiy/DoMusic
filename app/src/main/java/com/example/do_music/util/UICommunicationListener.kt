package com.example.do_music.util


interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun hideKeyboard()

    fun onAuthActivity()

    fun showNoInternetDialog()

    fun setLocale(language: String)

    fun getLocale(): String

}

interface UIMainCommunicationListener {

    fun downloadFile(uniqueName: String,fileName:String)

    fun enableWaiting()

    fun disableWaiting()

}

interface UIMainUpdate{

    fun setFavouriteUpdate(toUpdate:Boolean)

    fun getFavouriteUpdate():Boolean

    fun setVocalsUpdate(toUpdate:Boolean)

    fun getVocalsUpdate():Boolean

    fun setTheoryUpdate(toUpdate:Boolean)

    fun getTheoryUpdate():Boolean

    fun setInstrumentsUpdate(toUpdate:Boolean)

    fun getInstrumentsUpdate():Boolean

}
