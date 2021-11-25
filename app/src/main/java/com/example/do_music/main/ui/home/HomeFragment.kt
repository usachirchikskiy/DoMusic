package com.example.do_music.main.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.do_music.R
import com.example.do_music.databinding.FragmentHomeBinding
import com.example.do_music.databinding.FragmentLoginBinding
import com.example.do_music.main.ui.home.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    val home_elements = arrayOf(
        "Композиторы",
        "Теория",
        "Инструменты",
        "Вокал"
    )

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = activity?.let { ViewPagerAdapter(it.supportFragmentManager, lifecycle) }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = home_elements[position]
        }.attach()

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}