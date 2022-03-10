package com.example.do_music.presentation.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.DownloadFile
import com.example.do_music.util.Constants.Companion.DOWNLOAD_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject
constructor(
    private val downloadFile: DownloadFile
) : ViewModel() {
    val state: MutableLiveData<MainActivityState> = MutableLiveData(MainActivityState())
    val notificationState: MutableLiveData<NotificationState> = MutableLiveData(NotificationState())
    val downloadProgressState: MutableLiveData<Int> = MutableLiveData(-1)
    val updateState:UpdateState = UpdateState()
    var noInternet = false

    fun downloadFile(
        uniqueName: String,
        fileName: String,
        context: Context
    ) {
        state.value?.let { state ->
            downloadFile.downloadFile(uniqueName).onEach {
                if (it.isLoading) {
                    Log.d(TAG, "downloadFile: Begin")
                    beginNotification()
                }

                it.data?.let { responseBody ->
                    this.state.value =
                        state.copy(responseBody = responseBody, nameOfFile = fileName)
                    saveFile(context)
                }

                it.error?.let { error ->
                    Log.d(TAG, "downloadFile: $error")
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }

    }

    private fun saveFile(context: Context) {
        downloadProgressState.value?.let { downloadProgressState ->
            Log.d(TAG, "saveFile: ${state.value?.nameOfFile!!}")
            downloadFile.saveFile(
                responseBody = state.value?.responseBody!!,
                context = context,
                fileName = state.value?.nameOfFile!!
            ).onEach { progress ->
                if (progress % 20 == 0 && progress > 0) {
                    this.downloadProgressState.value = progress
                    Log.d(TAG, "saveFile: $progress")
                    delay(20)
                } else if (progress == -100) {
                    onCompleteNotification()
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun beginNotification() {
        notificationState.value?.let { notificationState ->
            this.notificationState.value = notificationState.copy(begin = true, onComplete = false)
        }
    }

    private fun onCompleteNotification() {
        notificationState.value?.let { notificationState ->
            this.notificationState.value = notificationState.copy(onComplete = true, begin = false)
        }
    }

    fun updateVocals(vocals:Boolean){
        updateState.vocals = vocals
    }

    fun updateInstruments(instruments:Boolean){
        updateState.instruments = instruments
    }

    fun updateTheory(theory:Boolean){
        updateState.theory = theory
    }

    fun updateFavourite(favourite:Boolean){
        updateState.favourite = favourite
    }

    fun clearValues() {
        state.value?.let { state ->
            this.state.value =
                state.copy(responseBody = null, nameOfFile = "", error = null)
        }
        notificationState.value?.let { notificationState ->
            this.notificationState.value =
                notificationState.copy(onComplete = false, begin = false)
        }
        downloadProgressState.value = -1
    }

}