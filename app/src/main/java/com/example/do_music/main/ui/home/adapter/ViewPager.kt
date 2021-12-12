package com.example.do_music.main.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.do_music.main.ui.home.ui.compositors.HomeCompositors
import com.example.do_music.main.ui.home.ui.instruments.InstrumentsFragment
import com.example.do_music.main.ui.home.ui.theory.TheoryFragment
import com.example.do_music.main.ui.home.ui.vocals.VocalsFragment

private const val NUM_TABS = 4

public class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeCompositors()
            1 -> return TheoryFragment()
            2 -> return InstrumentsFragment()
            3 -> return VocalsFragment()
        }
//        Change
        return HomeCompositors()
    }
}