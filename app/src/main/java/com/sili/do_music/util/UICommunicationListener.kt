package com.sili.do_music.util


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

    fun isLiked(favId: Int, isFav: Boolean, property: String)

}
