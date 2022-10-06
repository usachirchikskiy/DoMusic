package com.sili.do_music.business.interactors.account

import android.util.Log
import com.sili.do_music.business.datasources.data.account.UserAccountDao
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.business.model.main.TeacherAccount
import com.sili.do_music.business.datasources.network.main.account.UserChangeResponse
import com.sili.do_music.business.datasources.network.main.account.UserPhotoResponse
import com.sili.do_music.util.Constants.Companion.SUCCESS
import com.sili.do_music.util.Constants.Companion.SUCCESS_CODE
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Response

class GetUserAccount(
    private val userAccountDao: UserAccountDao,
    private val openMainApiService: OpenMainApiService
) {

    fun execute(): Flow<Resource<TeacherAccount>> = flow {
        emit(Resource.loading())
        var teacherAccount: TeacherAccount
        try {
            val userAccount = openMainApiService.getUserAccount()
            teacherAccount = openMainApiService.teacherAccount(userAccount.id.toString())
            userAccountDao.insertUserAccount(teacherAccount)

        } catch (throwable: Throwable) {
            emit(Resource.error(throwable))
        }
        teacherAccount = userAccountDao.getUserAccount()
        emit(Resource.success(teacherAccount))
    }.catch { error ->
        emit(Resource.error(error))
    }

    fun preparePassword(): Flow<Resource<UserChangeResponse>> = flow {
        emit(Resource.loading<UserChangeResponse>())
        val response = openMainApiService.passwordPrepare()
        emit(Resource.success(response))
    }.catch { e ->
        emit(Resource.error(e))
    }

    fun uploadPhotoToServer(image: MultipartBody.Part): Flow<Resource<UserPhotoResponse>> = flow {
        emit(Resource.loading())
        try {
            val uploadPhotoResponse = openMainApiService.uploadPhotoToServer(image)

            if (uploadPhotoResponse.code == SUCCESS_CODE) {
                emit(Resource.success(data = uploadPhotoResponse))
            }
        } catch (throwable: Throwable) {

            emit(
                Resource.error<UserPhotoResponse>(throwable)
            )
        }
    }

    fun uploadFilesToServer(
        comment: String,
        files: List<MultipartBody.Part>
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        try {
            val response: Response<String> = if (files.isEmpty()) {
                openMainApiService.uploadCommentToServer(comment)
            } else {
                openMainApiService.uploadFilesAndCommentToServer(comment = comment, file = files)
            }
            emit(Resource.success(SUCCESS))

        } catch (throwable: Throwable) {
            emit(
                Resource.error<String>(throwable)
            )
        }
    }

    fun changeNumber(number: String): Flow<Resource<TeacherAccount>> = flow {
        try {
            val userAccount = openMainApiService.userPhone(number)
            val phoneNumber = userAccount.phone
            val id = userAccount.id!!
            userAccountDao.changeUserPhone(phoneNumber!!, id)
        } catch (throwable: Throwable) {
            emit(Resource.error(throwable))
        }
        val userAccountChanged = userAccountDao.getUserAccount()
        emit(Resource.success(userAccountChanged))
    }.catch { error ->
        emit(Resource.error(error))
    }
}