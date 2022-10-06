package com.sili.do_music.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import com.sili.do_music.util.Constants.Companion.SELECTED_LANGUAGE
import java.util.*

class ContextUtils {
    companion object {

        fun onAttach(context: Context): Context {
            val lang = getPersistedData(context, Locale.getDefault().language);
            return setLocale(context, lang);
        }

        fun setLocale(context: Context, language: String): Context {
            persist(context, language);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return updateResources(context, language);
            }
            return updateResourcesLegacy(context, language);
        }

        private fun getPersistedData(context: Context, defaultLanguage: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(SELECTED_LANGUAGE, defaultLanguage).toString()
        }

        private fun persist(context: Context, language:String)
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(SELECTED_LANGUAGE, language);
            editor.apply();
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun updateResources(context: Context, language:String):Context
        {
            val locale = Locale(language)
            Locale.setDefault(locale);
            val configuration = context.resources.configuration;
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        }

        @SuppressWarnings("deprecation")
        fun updateResourcesLegacy(context: Context, language:String):Context
        {
            val locale = Locale(language);
            Locale.setDefault(locale);
            val resources = context.resources
            val configuration = resources.configuration
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.displayMetrics);
            return context
        }
    }
}

