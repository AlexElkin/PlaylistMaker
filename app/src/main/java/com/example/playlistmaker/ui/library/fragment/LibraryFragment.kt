package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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

    private val viewModel: LibraryViewModel by viewModel()
    private var tabMediator: TabLayoutMediator? = null


    private var _binding: LibraryFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LibraryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val viewPagerAdapter = FragmentViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter


        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator?.attach()

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "navigate_to_new_playlist",
            this
        ) { requestKey, bundle ->
            if (requestKey == "navigate_to_new_playlist") {
                findNavController().navigate(R.id.newPlaylistFragment)
            }
        }
    }

    fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_library_fragment_to_audio_player_fragment,
            bundleOf(TRACK to track)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator?.detach()
        tabMediator = null
        _binding = null
    }

    companion object {
        fun newInstance() = LibraryFragment()
    }
}