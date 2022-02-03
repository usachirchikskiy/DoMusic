package com.example.do_music.presentation.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.do_music.datastore.AppDataStore
import com.example.do_music.business.model.auth.UserAuthState
import com.example.do_music.util.Constants.Companion.LOGIN
import com.example.do_music.util.Constants.Companion.PASSWORD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SessionManagerTag"

@Singleton
class SessionManager @Inject constructor(
    private val appDataStoreManager: AppDataStore
) {
    private val sessionScope = CoroutineScope(Main)
    var state: MutableLiveData<UserAuthState> = MutableLiveData(UserAuthState())

    init {
        // Check if a user was authenticated in a previous session
        sessionScope.launch {
            val login = appDataStoreManager.readValue(LOGIN)
            val password = appDataStoreManager.readValue(PASSWORD)
            Log.d(TAG, "Session Init : $login,$password")
            if (!login.isNullOrBlank() && !password.isNullOrBlank()) {
                login(login, password)
            } else {
                startAuth()
            }
        }
    }

    private fun startAuth() {
        state.value?.let { state ->
            this.state.value = state.copy(
                onStarAuthActivity = true,
                onStarMainActivity = false
            )
        }
    }

    private fun startMain() {
        state.value?.let { state ->
            this.state.value = state.copy(
                onStarMainActivity = true,
                onStarAuthActivity = false
            )
        }
    }

    private fun setError(error: Throwable) {
        state.value?.let { state ->
            this.state.value = state.copy(
                error = error
            )
        }
    }

    fun setPassword(password: String) {
        state.value?.let { state ->
            this.state.value = state.copy(
                password = password
            )
        }
        sessionScope.launch {
            appDataStoreManager.setValue(PASSWORD, password)
        }
    }

    fun login(login: String, password: String) {
        state.value?.let { state ->
            this.state.value = state.copy(
                password = password,
                login = login
            )
        }
        sessionScope.launch {
            appDataStoreManager.setValue(LOGIN, login)
            appDataStoreManager.setValue(PASSWORD, password)
            startMain()
        }
    }

    fun clearValuesOfDataStore() {
        state.value?.let { state ->
            this.state.value = state.copy(
                password = "",
                login = "",
                onStarAuthActivity = false,
                onStarMainActivity = false
            )
        }
        sessionScope.launch {
            appDataStoreManager.setValue(LOGIN, "")
            appDataStoreManager.setValue(PASSWORD, "")
        }
    }

//
//    private fun checkPrevAuth(login: String, password: String) {
////        checkUserAuth.execute(login, password).onEach {
////            it.data?.let {
////                startMain()
////            }
////            it.error?.let { error ->
////                setError(error)
////            }
////
////        }.launchIn(sessionScope)
//    }
}
