package com.example.playlistmaker.ui.library.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.library.fragment.FragmentPlaylists
import com.example.playlistmaker.ui.library.fragment.FragmentTracks

class FragmentViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FragmentTracks.newInstance()
            else -> FragmentPlaylists.newInstance()
        }
    }
}