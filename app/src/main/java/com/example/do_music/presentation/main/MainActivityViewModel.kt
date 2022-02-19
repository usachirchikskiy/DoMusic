package com.example.do_music.presentation.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.DownloadFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject
constructor(
    private val downloadFile: DownloadFile
) : ViewModel() {
    val state: MutableLiveData<MainActivityState> = MutableLiveData(MainActivityState())
    val beginDownload: MutableLiveData<Boolean> = MutableLiveData(false)
    fun downloadFile(
        uniqueName: String,
        fileName: String,
        context: Context
    ) {
        beginDownload.value = true

//        viewModelScope.launch {
//            testFile()
//        }
        state.value?.let { state ->
            downloadFile.downloadFile(uniqueName).onEach {
                it.data?.let { responseBody ->
                    this.state.value =
                        state.copy(responseBody = responseBody, nameOfFile = fileName)
                    saveFile(context)
                }

                it.error?.let { error ->
                    this.state.value =
                        state.copy(error = error, nameOfFile = fileName)
                }

            }.launchIn(viewModelScope)
        }

    }

//    private suspend fun testFile(){
//
//        state.value?.let { state->
//            for(i in 1..100) {
//                delay(300)
//                this.state.value = state.copy(progress = i)
//            }
//        }
//    }

    private fun saveFile(context: Context) {
        state.value?.let { state ->
            downloadFile.saveFile(
                responseBody = state.responseBody!!,
                context = context,
                fileName = state.nameOfFile
            ).onEach { progress ->
                if(progress%25==0) {
                    Log.d(TAG, "saveFile: $progress")
                    if (progress == 100){
                        delay(200)
                    }
                    delay(200)
                }
                this.state.value = state.copy(progress = progress)
            }.launchIn(viewModelScope)
        }
    }

    fun clearValues() {
        state.value?.let { state ->
            this.state.value = state.copy(progress = 0, responseBody = null, nameOfFile = "",error = null)
        }
    }
}