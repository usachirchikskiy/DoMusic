package com.sili.do_music.presentation.auth.ui.login

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentLoginBinding
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.util.Constants.Companion.ABOUT_US_POLICY
import com.sili.do_music.util.Constants.Companion.NO_INTERNET
import com.sili.do_music.util.setGradient

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
        underLineLanguage()
        binding.loginBtn.setOnClickListener(this)
        setGradient(binding.forgotPassword)
        binding.forgotPassword.setOnClickListener(this)
        binding.confirm.setOnClickListener(this)
        binding.rus.setOnClickListener(this)
        binding.uzb.setOnClickListener(this)
        binding.ozb.setOnClickListener(this)
    }

    private fun underLineLanguage() {
        when(uiCommunicationListener.getLocale()){
            "ru" -> {
                binding.rus.setTextColor(Color.BLACK)
                binding.rus.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
            "en" -> {
                binding.ozb.setTextColor(Color.BLACK)
                binding.ozb.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
            else -> {
                binding.uzb.setTextColor(Color.BLACK)
                binding.uzb.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
        }
    }


    private fun subscribeObservers() {
        loginViewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)

            it.error?.let { error ->
                Log.d(TAG, "Error" + error.localizedMessage)
                if (error.localizedMessage.contains(NO_INTERNET)) {
                    uiCommunicationListener.showNoInternetDialog()
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
                val fileUrl = ABOUT_US_POLICY
                val uri = Uri.parse(fileUrl)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "text/html")
                startActivity(intent)
            }

            binding.rus->{
                uiCommunicationListener.setLocale("ru")
            }

            binding.uzb->{
                uiCommunicationListener.setLocale("uz")
            }

            binding.ozb->{
                uiCommunicationListener.setLocale("en")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}