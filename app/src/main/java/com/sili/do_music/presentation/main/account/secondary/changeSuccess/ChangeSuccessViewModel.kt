package com.sili.do_music.presentation.main.account.secondary.changeSuccess

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.auth.CheckUserAuth
import com.sili.do_music.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ChangeSuccessViewModel @Inject constructor(
    private val checkUserAuth: CheckUserAuth,
    private val sessionManager: SessionManager
) : ViewModel() {
    private var getChangeSuccessJob: Job? = null
    val state: MutableLiveData<Boolean> = MutableLiveData(false)

    fun logout() {
        getChangeSuccessJob?.cancel()
        getChangeSuccessJob = checkUserAuth.logout().onEach {
            it.data?.let {
                sessionManager.clearValuesOfDataStore()
                state.value = true
            }
        }.launchIn(viewModelScope)
    }
}