package com.sili.do_music.presentation.main.account.secondary.feedbackSuccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sili.do_music.databinding.FragmentFeedbackSuccessBinding
import com.sili.do_music.presentation.BaseFragment

class FeedbackSuccessFragment : BaseFragment() {
    private var _binding: FragmentFeedbackSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}