package com.sili.do_music.presentation.main.account.secondary.changeSuccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.databinding.FragmentChangeSuccessBinding
import com.sili.do_music.util.Constants.Companion.SUCCESS

private const val TAG = "ChangeSuccessFragment"

class ChangeSuccessFragment : BaseFragment(), View.OnClickListener{
    private var _binding: FragmentChangeSuccessBinding? = null
    private val binding get() = _binding!!
    private lateinit var successMessage: String
    private val changeSuccessViewModel: ChangeSuccessViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChangeSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        successMessage = arguments?.get(SUCCESS) as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        changeSuccessViewModel.state.observe(viewLifecycleOwner, Observer { state->
            if (state) {
                uiCommunicationListener.onAuthActivity()
            }
        })
    }

    private fun setupViews() {
        binding.changeText.text = successMessage
        binding.loginBtn.setOnClickListener(this)
    }

    fun startAuth(){
        changeSuccessViewModel.logout()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        startAuth()
    }


}