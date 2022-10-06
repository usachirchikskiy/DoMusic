package com.sili.do_music.presentation.main.account.secondary.changePassword.newPassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.common.PasswordCode
import com.sili.do_music.presentation.session.SessionManager
import com.sili.do_music.presentation.main.account.secondary.changePassword.checkCode.PasswordCheckState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    private val passwordCode: PasswordCode,
    private val sessionManager: SessionManager
) : ViewModel() {
    val state: MutableLiveData<PasswordCheckState> = MutableLiveData(PasswordCheckState())
    private var getNewPasswordJob: Job? = null

    fun execute(optCode: String, newPassword: String, repeatedNewPassword: String) {
        getNewPasswordJob?.cancel()
        state.value?.let { state ->
            getNewPasswordJob = passwordCode.confirmPassword(optCode, newPassword, repeatedNewPassword).onEach {
                this.state.value = state.copy(isLoading = it.isLoading)
                it.data?.let {
                    sessionManager.setPassword(newPassword)
                    this.state.value = state.copy(onComplete = true)
                }
                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }
}