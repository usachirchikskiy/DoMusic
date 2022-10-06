package com.sili.do_music.presentation.main.home.adapter

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sili.do_music.presentation.main.home.ui.compositors.HomeCompositors
import com.sili.do_music.presentation.main.home.ui.instruments.InstrumentsFragment
import com.sili.do_music.presentation.main.home.ui.theory.TheoryFragment
import com.sili.do_music.presentation.main.home.ui.vocals.VocalsFragment

private const val NUM_TABS = 4

class ViewPagerAdapter(@NonNull fragmentManager: FragmentManager, @NonNull lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> return HomeCompositors()
            1 -> return TheoryFragment()
            2 -> return InstrumentsFragment()
        }
        return VocalsFragment()
    }
}