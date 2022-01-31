package com.example.do_music.ui.main.account

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.account.GetUserAccount
import com.example.do_music.interactors.auth.CheckUserAuth
import com.example.do_music.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Job


private const val TAG = "AccountViewModel"

@HiltViewModel
class AccountViewModel
@Inject
constructor(
    private val getUserAccount: GetUserAccount,
    private val checkUserAuth: CheckUserAuth,
    private val sessionManager: SessionManager
) : ViewModel() {
    val state: MutableLiveData<UserAccountState> = MutableLiveData(UserAccountState())
    val update: MutableLiveData<Int> = MutableLiveData(0)

    init {
        Log.d(TAG, ": INIT")
        getUser()
    }

    fun logout() {
        state.value?.let { state ->
            checkUserAuth.logout().onEach {
                it.data?.let {
                    sessionManager.clearValuesOfDataStore()
                    this.state.value = state.copy(completed = true)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun preparePassword() {
        getUserAccount.preparePassword().launchIn(viewModelScope)
    }

    fun changeNumber(number: String) {
        state.value?.let { state ->
            getUserAccount.changeNumber(number).onEach {
                it.data?.let { userAccount ->
                    this.state.value = state.copy(userAccount = userAccount)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getUser() {
        state.value?.let { state ->
            getUserAccount.execute().onEach {

                Log.d(TAG, "getPage: " + it)

                it.data?.let { userAccount ->
                    this.state.value = state.copy(userAccount = userAccount)
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }


    private fun setEmptyList() {
        state.value?.let { state ->
            this.state.value = state.copy(filesPath = arrayListOf<String>())
            update.value = 0
        }

    }

    fun setData(data: String) {
        state.value?.let { state ->
            this.state.value = state.copy(data = data)
        }
    }

    fun uploadButtonClicked() {
        var listOfFiles = arrayListOf<MultipartBody.Part>()
        state.value?.let { state ->
            for (i in state.filesPath) {
                val uploadFile = File(i)

                val requestBody =
                    RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        uploadFile
                    )
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    uploadFile.name,
                    requestBody
                )
                listOfFiles.add(multipartBody)
            }
            getUserAccount.uploadFilesToServer(
                comment = state.data,
                files = listOfFiles
            ).onEach { dataState ->

                dataState.data?.let { response ->
                    Log.d(TAG, "uploadFiles: " + response)
                    setEmptyList()
                }

                dataState.error?.let { error ->
                    Log.d(TAG, "uploadFiles: " + error)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addToUploadFiles(selectedFilePath: String) {
        state.value?.let { state ->
            state.filesPath.add(selectedFilePath)
            update.value = update.value!! + 1
        }

    }
    private fun getExtension(fileName: String): String {
        val arrayOfFilename = fileName.toCharArray()
        for (i in arrayOfFilename.size - 1 downTo 1) {
            if (arrayOfFilename[i] == '.') {
                return fileName.substring(i + 1, fileName.length)
            }
        }
        return ""
    }

    private fun getMimeType(file: File): String? {
        var mimeType: String? = ""
        val extension: String = getExtension(file.name)
        if (MimeTypeMap.getSingleton().hasExtension(extension)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return mimeType
    }

    fun uploadPhoto(selectedImagePath: String) {
        state.value?.let {
            val imageFile = File(selectedImagePath)
            val contentDisposition = getMimeType(imageFile)
            Log.d(TAG, "uploadPhoto: " + contentDisposition)
            val requestBody =
                RequestBody.create(
                    MediaType.parse(contentDisposition),
                    imageFile
                )
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                requestBody
            )
            Log.d(TAG, "uploadPhoto: " + multipartBody.headers())
            getUserAccount.uploadPhotoToServer(
                image = multipartBody,
            ).onEach { dataState ->

                dataState.data?.let { response ->
                    Log.d(TAG, "uploadPhoto: " + response)
                }

                dataState.error?.let { error ->
                    Log.d(TAG, "uploadPhoto: " + error)
                }
            }.launchIn(viewModelScope)
        }
    }
}
