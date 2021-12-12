package com.example.do_music.auth.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.do_music.databinding.FragmentLoginBinding
import com.example.do_music.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.loginBtn.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        loginViewModel.login_boolean.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                requireActivity().run {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // If activity no more needed in back stack
                }
            }
        })
    }


    fun String.isValid(): Boolean = this.isNotEmpty()

    override fun onClick(p0: View?) {
        binding.paginationProgressBar.visibility = View.VISIBLE
        if (binding.loginEt.text.toString().isValid() && binding.passwordEt.text.toString()
                .isValid()
        ) {
            loginViewModel.login(
                binding.loginEt.text.toString(),
                binding.passwordEt.text.toString()
            )
        } else {
            Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
    }

}