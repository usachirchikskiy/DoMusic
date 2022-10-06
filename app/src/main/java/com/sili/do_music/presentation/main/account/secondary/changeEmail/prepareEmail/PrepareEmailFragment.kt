package com.sili.do_music.presentation.main.account.secondary.changeEmail.prepareEmail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentPrepareEmailBinding

import com.sili.do_music.util.Constants

private const val TAG = "PrepareEmailFragment"
class PrepareEmailFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentPrepareEmailBinding? = null
    private val binding get() = _binding!!
    private val prepareEmailViewModel: PrepareEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrepareEmailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        uiMainCommunicationListener.showBottomNavigation(false)
        setupViews()
        setupObservers()
        Log.d(TAG, "onViewCreated: ")
    }

    private fun setupViews() {
        binding.continueBtn.setOnClickListener(this)
    }

    private fun setupObservers() {
        prepareEmailViewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)

            if(it.onComplete){
                val bundle = bundleOf(Constants.SUCCESS to getString(R.string.change_email_success))
                findNavController().navigate(
                    R.id.action_prepareEmailFragment_to_changeSuccessFragment,
                    bundle
                )
//                prepareEmailViewModel.setCompletedToFalse()
            }

            it.error?.let { error->
                if(error.localizedMessage == Constants.INCORRECT_CODE){
                    binding.incorrectCredentials.visibility = View.VISIBLE
                }
                prepareEmailViewModel.setErrorNull()
            }
        })
    }

    override fun onClick(v: View?) {
        val code = binding.passwordEt.text.toString()
        if(!code.isNullOrBlank()){
            prepareEmailViewModel.checkPassword(code)
        }else{
            binding.passwordEt.setHintTextColor(Color.RED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

}