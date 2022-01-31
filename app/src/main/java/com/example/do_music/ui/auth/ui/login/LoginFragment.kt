package com.example.do_music.ui.auth.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.do_music.R
import com.example.do_music.databinding.FragmentLoginBinding
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.ui.noInternet
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.NO_INTERNET
import com.example.do_music.util.setgradient

private const val TAG = "LoginFragment"

class LoginFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        setupViews()
    }

    private fun setupViews() {
        binding.loginBtn.setOnClickListener(this)
        setgradient(binding.forgotPassword)
        binding.forgotPassword.setOnClickListener(this)
        binding.confirm.setOnClickListener(this)
    }

    private fun subscribeObservers() {
        loginViewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)
            it.error?.let { error ->
                Log.d(TAG, "Error" + error.localizedMessage)
                if (error.localizedMessage.contains(NO_INTERNET)) {
                    noInternet(context)
                }
                else {
                    showError()
                }

            }

        })
    }

    private fun showError() {
        binding.incorrectCredentials.visibility = View.VISIBLE
    }

    private fun String.isValid(): Boolean = this.isNotEmpty()

    override fun onClick(v: View?) {
        when (v) {
            binding.loginBtn -> {
                if (binding.loginEt.text.toString().isValid() && binding.passwordEt.text.toString()
                        .isValid()
                ) {
                    loginViewModel.login(
                        binding.loginEt.text.toString(),
                        binding.passwordEt.text.toString()
                    )
                }
                else{
                    showError()
                }
            }

            binding.forgotPassword->{
                findNavController().navigate(R.id.action_loginFragment_to_forgotPassword)
            }

            binding.confirm->{
                Log.d(TAG, "onClick: ")
                val fileUrl = "http://docs.google.com/viewer?url=https://domusic.uz/static/docs/confidential_ru.pdf"
                val uri = Uri.parse(fileUrl)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "text/html")
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}