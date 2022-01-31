package com.example.do_music.ui.auth.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.auth.CheckUserAuth
import com.example.do_music.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkUserAuth: CheckUserAuth,
    private val sessionManager: SessionManager
) : ViewModel() {
    private var getLoginJob: Job? = null
    val state: MutableLiveData<LoginState> = MutableLiveData(LoginState())

    fun login(login: String, password: String) {
        getLoginJob?.cancel()
        state.value?.let { state ->
            getLoginJob = checkUserAuth.execute(login, password).onEach {
                this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let {
                    sessionManager.login(login, password)
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }
}