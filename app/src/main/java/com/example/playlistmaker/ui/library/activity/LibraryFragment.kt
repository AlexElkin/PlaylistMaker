package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
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
    fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_library_fragment_to_audio_player_fragment, // Добавьте этот action!
            bundleOf(TRACK to track)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}

