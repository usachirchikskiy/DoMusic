package com.example.do_music.auth.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.do_music.network.auth.OpenAuthApiService
import com.example.do_music.session.SessionManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: OpenAuthApiService,
    private val sessionManager: SessionManager
) {
    var login_boolean : MutableLiveData<Boolean> = MutableLiveData()

    fun login(log: String, password: String){
        login_boolean.value = false
        val jsonObject = JSONObject()
        jsonObject.put("login", log)
        jsonObject.put("password", password)
        api.login(jsonObject.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.string()?.let {

                    if (it.equals("Success")) {
                        login_boolean.value = true
                        sessionManager.logout()
                        sessionManager.logged(log,password)
                    }
                }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                login_boolean.value = false
                t.message?.let { Log.d("Error", it) }
            }
        }
        )
    }


}