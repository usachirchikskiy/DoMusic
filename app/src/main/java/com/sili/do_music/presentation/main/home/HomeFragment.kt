package com.sili.do_music.presentation.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentHomeBinding
import com.sili.do_music.presentation.BaseFragment

import com.sili.do_music.presentation.main.home.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator



class HomeFragment : BaseFragment() {
    private lateinit var homeElements:Array<String>
    private var mediator: TabLayoutMediator? = null
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeElements = arrayOf(
            getString(R.string.compositors_tab),
            getString(R.string.theory_tab),
            getString(R.string.instruments_tab),
            getString(R.string.vocal_tab),
        )
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        viewPager!!.adapter = ViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        mediator = TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = homeElements[position]
        }
        mediator!!.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager?.let {
            it.adapter = null
        }
        viewPager = null
        mediator?.detach()
        mediator = null
        tabLayout = null
        _binding = null

    }
}