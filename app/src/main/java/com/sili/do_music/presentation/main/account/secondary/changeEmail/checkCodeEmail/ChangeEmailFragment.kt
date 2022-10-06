package com.sili.do_music.presentation.main.account.secondary.changeEmail.checkCodeEmail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentChangeEmailBinding
import com.sili.do_music.util.Constants.Companion.INCORRECT_EMAIL
import com.sili.do_music.util.accountExistsDialog


class ChangeEmailFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!
    private val changeEmailViewModel: ChangeEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeEmailBinding.inflate(layoutInflater)
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
        changeEmailViewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)

            if(it.onComplete){
                findNavController().navigate(
                    R.id.action_changeEmailFragment_to_prepareEmailFragment
                )
            }

            it.error?.let { error->
                // TODO
//                Log.d(TAG, "setupObservers: ")
                if(error.localizedMessage == INCORRECT_EMAIL){
                    binding.sameEmail.visibility = View.VISIBLE
                }
                else if(error.localizedMessage == "HTTP 409 "){
                    accountExistsDialog(context)
                }
                changeEmailViewModel.setErrorNull()
            }
        })
    }

    override fun onClick(v: View?) {
         val email = binding.emaiEt.text.toString()
        if(!email.isNullOrBlank()){
            changeEmailViewModel.enterEmail(email)
        }else{
            binding.emaiEt.setHintTextColor(Color.RED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}