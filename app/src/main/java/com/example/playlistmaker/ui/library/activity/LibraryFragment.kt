package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.LibraryFragmentBinding
import com.example.playlistmaker.ui.library.adapter.FragmentViewPagerAdapter
import com.example.playlistmaker.ui.settings.viewmodel.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private lateinit var binding: LibraryFragmentBinding
    private lateinit var tabMediator: TabLayoutMediator
    private val viewModel: LibraryViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        binding = LibraryFragmentBinding.inflate(inflater, container, false)
        binding.viewPager.adapter = FragmentViewPagerAdapter(childFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}

