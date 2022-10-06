package com.sili.do_music.presentation.main.account.secondary.changeEmail.checkCodeEmail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.common.EmailCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "ChangeEmailViewModel"
@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val emailCode: EmailCode
) : ViewModel() {
    val state: MutableLiveData<EmailCheckState> = MutableLiveData(EmailCheckState())
    private var getChangeEmailJob: Job? = null
//    fun setCompletedToFalse() {
//        state.value?.let { state ->
//            this.state.value = state.copy(onComplete = false)
//        }
//    }

    fun enterEmail(email: String) {
        getChangeEmailJob?.cancel()
        state.value?.let { state ->
            getChangeEmailJob = emailCode.execute(email).onEach {
                Log.d(TAG, "enterEmail: " + it.toString())
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

    fun setErrorNull() {
        state.value?.let { state->
            this.state.value = state.copy(error = null)
        }
    }
}