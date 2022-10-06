package com.sili.do_music.presentation.main.account.secondary.changePassword.checkCode

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.common.PasswordCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ChangePasswordViewModel"

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val passwordCode: PasswordCode
) : ViewModel() {
    val state: MutableLiveData<PasswordCheckState> = MutableLiveData(PasswordCheckState())
    private var getChangePasswordJob: Job? = null

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }
    fun setCode(code: String) {
        state.value?.let { state ->
            this.state.value = state.copy(code = code)
        }
    }

    fun checkPassword() {
        getChangePasswordJob?.cancel()
        state.value?.let { state ->
            getChangePasswordJob = passwordCode.execute(state.code).onEach {
                Log.d(TAG, "checkPassword: " + it.toString())
                this.state.value = state.copy(isLoading = it.isLoading)
                it.data?.let {
                    this.state.value = state.copy(onComplete = true)
                }
                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }
}