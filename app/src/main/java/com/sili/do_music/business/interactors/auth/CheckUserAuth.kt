package com.sili.do_music.business.interactors.auth

import com.sili.do_music.business.datasources.data.account.UserAccountDao
import com.sili.do_music.business.datasources.data.favourites.FavouritesDao
import com.sili.do_music.business.datasources.data.home.compositors.CompositorsDao
import com.sili.do_music.business.datasources.data.home.instruments.InstrumentsDao
import com.sili.do_music.business.datasources.data.home.theory.TheoryDao
import com.sili.do_music.business.datasources.data.home.vocal.VocalsDao
import com.sili.do_music.business.datasources.network.auth.OpenAuthApiService
import com.sili.do_music.util.Constants.Companion.EMAIL
import com.sili.do_music.util.Constants.Companion.FIO
import com.sili.do_music.util.Constants.Companion.LOGIN
import com.sili.do_music.util.Constants.Companion.PASSWORD
import com.sili.do_music.util.Constants.Companion.PHONE
import com.sili.do_music.util.Constants.Companion.SUCCESS
import com.sili.do_music.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject


class CheckUserAuth(
    private val api: OpenAuthApiService,
    private val userAccountDao: UserAccountDao,
    private val favouritesDao: FavouritesDao,
    private val compositorsDao: CompositorsDao,
    private val instrumentsDao: InstrumentsDao,
    private val theoryDao: TheoryDao,
    private val vocalsDao: VocalsDao,
) {

    fun logout(): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        try {
            api.logout()
            emit(Resource.success(SUCCESS))
        } catch (throwable: Exception) {
            emit(
                Resource.error<String>(throwable)
            )
        }

    }

    fun loginRestore(
        fio: String,
        phone: String,
        email: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.loading())
        val jsonObject = JSONObject()
        jsonObject.put(FIO, fio)
        jsonObject.put(PHONE, phone)
        jsonObject.put(EMAIL, email)
        try {
            val response = api.loginRestore(jsonObject.toString())
            if (response) {
                emit(Resource.success(response))
            }
        } catch (throwable: Exception) {
            emit(
                Resource.error<Boolean>(throwable)
            )
        }
    }

    fun execute(
        login: String,
        password: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        val jsonObject = JSONObject()
        jsonObject.put(LOGIN, login)
        jsonObject.put(PASSWORD, password)
        try {
            val response = api.login(jsonObject.toString())
            if (response == SUCCESS) {
                userAccountDao.deleteUserAccount()
                compositorsDao.deleteAllCompositors()
                vocalsDao.deleteAllVocals()
                instrumentsDao.deleteAllInstruments()
                theoryDao.deleteAllBooks()
                favouritesDao.deleteAllFavourites()
                emit(Resource.success(SUCCESS))
            }
        } catch (throwable: Exception) {
            emit(
                Resource.error<String>(throwable)
            )
        }
    }
}

