package com.sili.do_music.presentation.auth.ui.forgot_password.primary

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.auth.CheckUserAuth
import com.sili.do_music.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ForgotPasswordViewModel"
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val checkUserAuth: CheckUserAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    val state: MutableLiveData<ForgotPasswordState> = MutableLiveData(ForgotPasswordState())

    fun restoreLogin(
        fio: String,
        phone: String,
        email: String
    ){
        state.value?.let { state ->
           checkUserAuth.loginRestore(fio, phone,email).onEach {
               Log.d(TAG, "restoreLogin: " + it)
                it.data?.let {
                    this.state.value = state.copy(onComplete = true)
                    sessionManager.clearValuesOfDataStore()
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }
}