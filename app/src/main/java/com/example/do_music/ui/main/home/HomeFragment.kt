package com.example.do_music.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.do_music.databinding.FragmentHomeBinding
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.ui.main.home.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


private const val TAG = "HomeFragment"

class HomeFragment : BaseFragment() {
    val home_elements = arrayOf(
        "Композиторы",
        "Теория",
        "Инструменты",
        "Вокал"
    )

    private var mediator: TabLayoutMediator? = null
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null

    //    private var adapter: ViewPagerAdapter?=null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

//    private val homeCompositorViewModel: HomeCompositorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        viewPager!!.adapter = ViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        mediator = TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = home_elements[position]
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