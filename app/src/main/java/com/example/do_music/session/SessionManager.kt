package com.example.do_music.session

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor)
{
    fun logged(login:String,password:String){
        editor.putString("login", login)
        editor.putString("password",password)
        editor.commit()
    }

    fun logout(){
        editor.clear()
        editor.commit()
    }

    fun getlogin(): String? {
        Log.d("Boolean",sharedPreferences.getString("login", null).toString())
        return sharedPreferences.getString("login", null)
    }


    fun getpassword():String?{
        Log.d("Boolean",sharedPreferences.getString("password", null).toString())
        return sharedPreferences.getString("password", null)
    }

    fun isnull():Boolean{
        val email = sharedPreferences.getString("login", null);
        val password = sharedPreferences.getString("password", null);

        if (email != null && password != null) {
            return false
        }
        return true
    }

}