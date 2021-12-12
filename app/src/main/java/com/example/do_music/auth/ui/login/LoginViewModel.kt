package com.example.do_music.auth.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.do_music.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {


    var login_boolean : MutableLiveData<Boolean> = authRepository.login_boolean
    fun login(log:String,password:String)=authRepository.login(log,password)
}