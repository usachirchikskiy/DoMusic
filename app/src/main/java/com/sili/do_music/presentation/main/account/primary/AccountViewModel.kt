package com.sili.do_music.presentation.main.account.primary

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.account.GetUserAccount
import com.sili.do_music.business.interactors.auth.CheckUserAuth
import com.sili.do_music.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


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
    val thankForFeedBackState: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
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

    private fun getUser() {
        state.value?.let { state ->
            getUserAccount.execute().onEach {

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
            this.state.value = state.copy(filesPath = arrayListOf())
            update.value = 0
        }

    }

    fun setData(data: String) {
        state.value?.let { state ->
            this.state.value = state.copy(data = data)
        }
    }

    fun uploadButtonClicked() {
        val listOfFiles = arrayListOf<MultipartBody.Part>()
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
                    Log.d(TAG, "uploadFiles: $response")
                    setEmptyList()
                    setFeedbackOnComplete()
                }

                dataState.error?.let { error ->
                    Log.d(TAG, "uploadFiles: $error")
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addToUploadFiles(selectedFilePath: String) {
        Log.d(TAG, "addToUploadFiles: $selectedFilePath")
        state.value?.let { state ->
            state.filesPath.add(selectedFilePath)
            update.value = update.value!! + 1
        }

    }

    private fun setFeedbackOnComplete() {
        thankForFeedBackState.value = true
    }

    fun setFeedbackOnRestore() {
        thankForFeedBackState.value = false
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
            val file = File(selectedImagePath)
            val contentDisposition = getMimeType(file)
            val requestBody =
                RequestBody.create(
                    MediaType.parse(contentDisposition),
                    file
                )
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestBody
            )
            Log.d(TAG, "uploadPhoto: " + multipartBody.headers())
            getUserAccount.uploadPhotoToServer(
                image = multipartBody,
            ).onEach { dataState ->

                dataState.data?.let { response ->
                    Log.d(TAG, "uploadPhoto: $response")
//                    getUser()
                }

                dataState.error?.let { error ->
                    Log.d(TAG, "uploadPhoto: $error")
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setUri(uri: Uri) {
        state.value?.let { state ->
            this.state.value = state.copy(uri = uri)
        }
    }

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }


}
