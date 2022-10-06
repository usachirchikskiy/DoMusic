package com.sili.do_music.presentation.main.account.secondary.changePassword.newPassword

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentNewPasswordBinding
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.CODE
import com.sili.do_music.util.Constants.Companion.SUCCESS
import com.sili.do_music.util.passwordExistsDialog


class NewPasswordFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var code: String
    private val newPasswordViewModel: NewPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code = arguments?.get(CODE) as String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupViews()
    }

    private fun setupViews() {
        binding.loginBtn.setOnClickListener(this)
    }

    private fun setupObservers() {
        newPasswordViewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)

            if (it.onComplete) {
                val bundle = bundleOf(SUCCESS to getString(R.string.change_password_success))
                findNavController().navigate(
                    R.id.action_newPasswordFragment_to_changeSuccessFragment,
                    bundle
                )
            }

            it.error?.let { error ->
                if(error.localizedMessage == Constants.INCORRECT_CODE){
                    binding.incorrectCredentials.visibility = View.VISIBLE
                }
                else if(error.localizedMessage == "HTTP 406 "){
                    passwordExistsDialog(context)
                }

                newPasswordViewModel.setErrorNull()
            }
        })
    }

    override fun onClick(v: View?) {
        val newPassword = binding.newPasswordEt.text.toString()
        val confirmPassword = binding.passwordEt.text.toString()
        if (!newPassword.isNullOrBlank() && !confirmPassword.isNullOrBlank()) {
            if (newPassword == confirmPassword) {
                newPasswordViewModel.execute(code, newPassword, confirmPassword)
            }
            else{
                binding.incorrectCredentials.visibility = View.VISIBLE
            }
        }
        else{
            binding.passwordEt.setHintTextColor(Color.RED)
            binding.newPasswordEt.setHintTextColor(Color.RED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}