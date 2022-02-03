package com.example.do_music

import android.content.Context
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class DoMusicApplication : LocalizationApplication() {
    override fun getDefaultLanguage(context: Context): Locale = Locale.ENGLISH
}