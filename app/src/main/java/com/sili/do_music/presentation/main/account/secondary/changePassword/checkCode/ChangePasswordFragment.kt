package com.sili.do_music.presentation.main.account.secondary.changePassword.checkCode

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
import com.sili.do_music.databinding.FragmentChangePasswordBinding

import com.sili.do_music.util.Constants.Companion.CODE
import com.sili.do_music.util.Constants.Companion.INCORRECT_CODE

private const val TAG = "ChangePasswordFragment"

class ChangePasswordFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()
    private lateinit var code: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.continueBtn.setOnClickListener(this)
    }

    private fun setupObservers() {
        changePasswordViewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)

            if (it.onComplete) {
                val bundle = bundleOf(CODE to code)
                findNavController().navigate(
                    R.id.action_changePasswordFragment_to_newPasswordFragment,
                    bundle
                )
            }

            it.error?.let { error ->
                // TODO
                if(error.localizedMessage == INCORRECT_CODE){
                    binding.incorrectCredentials.visibility = View.VISIBLE
                }

                changePasswordViewModel.setErrorNull()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        code = binding.passwordEt.text.toString()
        if (!code.isNullOrBlank()) {
            changePasswordViewModel.setCode(code)
            changePasswordViewModel.checkPassword()
        }
        else{
            binding.passwordEt.setHintTextColor(Color.RED)
        }
    }
}