package com.sili.do_music.presentation.auth.ui.forgot_password.secondary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sili.do_music.databinding.FragmentSuccessBinding
import com.sili.do_music.presentation.BaseFragment

class SuccessFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.continueBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        uiCommunicationListener.onAuthActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}