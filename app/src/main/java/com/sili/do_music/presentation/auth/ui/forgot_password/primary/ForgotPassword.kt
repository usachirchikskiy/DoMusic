package com.sili.do_music.presentation.auth.ui.forgot_password.primary

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentForgotPasswordBinding
import com.sili.do_music.presentation.BaseFragment

private const val TAG = "ForgotPassword"
class ForgotPassword : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        setupViews()
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer { 
            if(it.onComplete){
                findNavController().navigate(R.id.action_forgotPassword_to_successFragment)
            }
            it.error?.let {
                Log.d(TAG, "subscribeObservers: " + it.localizedMessage)
            }
            
        })
    }

    private fun setupViews() {
        binding.sendBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val fio = binding.fioEt.text.toString()
        val phone = binding.phoneNumberEt.text.toString()
        val email =  binding.emailEt.text.toString()
        if(fio.isNotBlank() && email.isNotBlank() && phone.isNotBlank()) {
            Log.d(TAG, "onClick: ")
            viewModel.restoreLogin(fio, phone, email)
        }
        else{
            if(fio.isBlank()){
                binding.fioEt.setHintTextColor(Color.RED)
            }
            else if(email.isBlank()){
                binding.emailEt.setHintTextColor(Color.RED)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}