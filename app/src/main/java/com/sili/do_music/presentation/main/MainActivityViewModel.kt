package com.sili.do_music.presentation.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.common.AddToFavourite
import com.sili.do_music.business.interactors.common.DownloadFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject
constructor(
    private val downloadFile: DownloadFile,
    private val addToFavourite: AddToFavourite
) : ViewModel() {
    val state = MutableLiveData(MainActivityState())
    val notificationState = MutableLiveData(NotificationState())
    private var updateJob: Job? = null

    fun isLiked(favId: Int, isFav: Boolean, property: String) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            state.value?.let { st ->
                viewModelScope.launch {
                    val resource = addToFavourite.execute(
                        id = favId,
                        isFavourite = isFav,
                        property = property
                    )
                    resource.data?.let {
                        Log.d(TAG, "isLiked: $it")
                    }

                    resource.error?.let {
                        state.value = st.copy(error = it)
                    }
                }
            }
        }
    }

    fun downloadFile(
        uniqueName: String,
        fileName: String
    ) {
        state.value?.let { state ->
            downloadFile.saveFile(
                uniqueName,
                fileName
            ).onEach {
                if (it.isLoading) {
                    beginNotification()
                }

                it.data?.let {
                    this.state.value = state.copy(fileName = fileName)
                    onCompleteNotification()
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }

    }

    private fun beginNotification() {
        notificationState.value?.let { notificationState ->
            this.notificationState.value =
                notificationState.copy(begin = true, onComplete = false)
        }
    }

    private fun onCompleteNotification() {
        notificationState.value?.let { notificationState ->
            this.notificationState.value =
                notificationState.copy(onComplete = true, begin = false)
        }
    }

    fun clearValues() {
        state.value?.let { state ->
            this.state.value =
                state.copy(error = null, fileName = "",internet = false)
        }
        notificationState.value?.let { notificationState ->
            this.notificationState.value =
                notificationState.copy(onComplete = false, begin = false)
        }
    }

    fun setStateInternet(){
        state.value?.let { state ->
            this.state.value =
                state.copy(internet = true)
        }
    }

}