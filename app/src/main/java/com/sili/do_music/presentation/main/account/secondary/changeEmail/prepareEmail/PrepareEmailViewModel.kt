package com.sili.do_music.presentation.main.account.secondary.changeEmail.prepareEmail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.common.EmailCode
import com.sili.do_music.presentation.main.account.secondary.changeEmail.checkCodeEmail.EmailCheckState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "PrepareEmailViewModel"

@HiltViewModel
class PrepareEmailViewModel @Inject constructor(
    private val emailCode: EmailCode
) : ViewModel() {
    val state: MutableLiveData<EmailCheckState> = MutableLiveData(EmailCheckState())
    private var getPrepareEmailJob: Job? = null

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }

    fun checkPassword(code: String) {
        getPrepareEmailJob?.cancel()
        state.value?.let { state ->
            getPrepareEmailJob = emailCode.confirmEmail(code).onEach {
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